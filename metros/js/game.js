import * as data from '../data/stations.json' with { type: "json" };
const { default: stationData } = data;

import * as data2 from '../data/lines.json' with { type: "json" };
const { default: metros } = data2;

const params = new URLSearchParams(window.location.search);
const nameInput = params.get('nameInput');
const cardPlace = document.querySelector("#card");
const table = document.querySelector("#tbody");
const metroCard = document.querySelector("#metro");
const pointsBoard = document.querySelector('#points_board');
const cards = ["A", "A", "B", "B", "C", "C", "D", "D", "Joker", "Joker"];
const timer = setInterval(tick, 1000);  

const DIR_VECTORS = [
  {dx: 1, dy: 0},   // 0 right
  {dx: 1, dy: 1},   // 1 down-right 
  {dx: 0, dy: 1},   // 2 down
  {dx: -1, dy: 1},  // 3 down-left
  {dx: -1, dy: 0},  // 4 left
  {dx: -1, dy: -1}, // 5 up-left
  {dx: 0, dy: -1},  // 6 up
  {dx: 1, dy: -1}   // 7 up-right
];

let currentStation;
let currentMetro;
let currentCard;
let shuffleCards;
let shuffleMetros;
let metroEnds = [];
let metroLines = [];
let oneMetroLine =[];
let elapsedSeconds = 0;
let lineIndex = 0;
let PK, PM, PD;
let FP = 0;


startGame()
function startGame(){
    document.querySelector('#name').textContent = `Játékos: ${nameInput}`;

    shuffleMetros = shuffleArray([...metros]);

    shuffleCards = shuffleArray([...cards]);
    
    currentMetro = shuffleMetros.pop();
    oneMetroLine.push({...stationData.find(s => s.id === currentMetro.start)});

    renderTable();
    metroEnds.push({ ...stationData.find(s => s.id === currentMetro.start) });
    currentStation = stationData.find(s => s.id === currentMetro.start);
    
    currentCard = shuffleCards.pop();
    changeCard(currentCard);
        
}

function pointCount(){
    //PK
    const dist = new Set(oneMetroLine.map(s => s.district));
    PK = dist.size;

    //PM
    const districtCounts = {};

    oneMetroLine.forEach(station => {
        const d = station.district;
        if (districtCounts[d]) {
            districtCounts[d]++;
        } else {
            districtCounts[d] = 1;
        }
    });
    PM = Math.max(...Object.values(districtCounts));

    //PD
    PD = 0;
    for(let i = lineIndex; i < metroLines.length; i++){
        if(metroLines[i].start.side !== metroLines[i].destination.side)
            PD++;
    }

    FP += PK * PM + PD;
}

function renderPointTable(){
    const imgNames = ["Dist", "MaxDist", "Duna"];
    
    for (let rowIndex = 0; rowIndex < 4; rowIndex++) {
        const row = document.createElement('div');
        row.className = 'row';

        for (let colIndex = 0; colIndex < 5; colIndex++) {
            const tile = document.createElement('div');
            tile.className = 'big-tile';
            tile.id = `tile-${rowIndex}-${colIndex}`;
            
            if (colIndex === 0 && rowIndex < 3) {
                tile.style.backgroundColor = "rgba(76, 14, 95, 1)";

                const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
                svg.setAttribute("width", "46");  
                svg.setAttribute("height", "46");

                const topIcon = document.createElementNS("http://www.w3.org/2000/svg", "image");
                topIcon.setAttributeNS("http://www.w3.org/1999/xlink", "href", `../img/${imgNames[rowIndex]}.svg`);
                topIcon.setAttribute("width", 46);
                topIcon.setAttribute("height", 46);
                topIcon.setAttribute("x", 0);  
                topIcon.setAttribute("y", 0);  

                svg.appendChild(topIcon);
                tile.appendChild(svg);
            }
            if (colIndex === 0 && rowIndex === 3)
                tile.style.backgroundColor = "rgba(76, 14, 95, 1)";

            row.appendChild(tile);
        }

        pointsBoard.appendChild(row);

        if (rowIndex < 3) {
            const opRow = document.createElement('div');
            opRow.className = 'row operators';
            for (let colIndex = 0; colIndex < 5; colIndex++) {
                const opDiv = document.createElement('div');
                opDiv.className = 'operator';
                if (colIndex > 0) {
                    if (rowIndex === 0) opDiv.textContent = '×';
                    if (rowIndex === 1) opDiv.textContent = '+';
                    if (rowIndex === 2) opDiv.textContent = '=';
                }
                opRow.appendChild(opDiv);
            }
            pointsBoard.appendChild(opRow);
        }

    }
}


function fillInPoints(){
    const round = 4 - shuffleMetros.length;
    const pointPK = pointsBoard.querySelector(`#tile-0-${round}`);
    pointPK.textContent = PK;

    const pointPM = pointsBoard.querySelector(`#tile-1-${round}`);
    pointPM.textContent = PM;

    const pointPD = pointsBoard.querySelector(`#tile-2-${round}`);
    pointPD.textContent = PD;

    const pointFP = pointsBoard.querySelector(`#tile-3-${round}`);
    pointFP.textContent = FP;

}

function oppositeDir(dir) {
  return (dir + 4) % 8;
}

function nextTurn(){
    currentStation = 0;

    if(shuffleCards.length === 0){
        
        nextRound();
        return;
    }
    else{
        currentCard = shuffleCards.pop();
        changeCard(currentCard);
    }
    
    
}

function nextRound(){
    if(shuffleMetros.length === 0){
        pointCount();
        fillInPoints();
        return;
    }
    pointCount();
    fillInPoints();
    shuffleCards = shuffleArray([...cards]);
    currentCard = shuffleCards.pop();
    currentMetro = shuffleMetros.pop();
    metroEnds = [];
    metroEnds = [stationData.find(s => s.id === currentMetro.start)];
    currentStation = stationData.find(s => s.id === currentMetro.start);
    oneMetroLine = [{ ...stationData.find(s => s.id === currentMetro.start) }];
    highlightMetro(currentMetro);
    changeMetroCard();
    changeCard(currentCard);
    PK = 0;
    PM = 0;
    PD = 0;
}

function renderTable(){
    renderBody();
    renderStations();
    renderStationStarts();
    highlightMetro(currentMetro);
    renderCard();
    renderMetroCard();
    renderPointTable();
}

function renderBody() {
    const cellSize = 58;

    for (let y = 0; y < 10; y++) {
        const row = document.createElement("tr");

        for (let x = 0; x < 10; x++) {
            const cell = document.createElement("td");

            const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
            svg.setAttribute("width", cellSize);
            svg.setAttribute("height", cellSize);
            svg.setAttribute("viewBox", `0 0 ${cellSize} ${cellSize}`);

            const cx = cellSize / 2;
            const cy = cellSize / 2;

            for (let dir = 0; dir < 8; dir++) {
                const {dx, dy} = DIR_VECTORS[dir];

                let x2 = cx + dx * (cellSize / 2);
                let y2 = cy + dy * (cellSize / 2);

                if (dx !== 0 && dy !== 0) {
                    const diag = (cellSize * Math.sqrt(2)) / 2;
                    x2 = cx + dx * diag;
                    y2 = cy + dy * diag;
                }

                const line = document.createElementNS("http://www.w3.org/2000/svg", "line");
                line.setAttribute("x1", cx);
                line.setAttribute("y1", cy);
                line.setAttribute("x2", x2);
                line.setAttribute("y2", y2);

                line.setAttribute("stroke", "rgba(222, 222, 222, 0.5)");
                line.setAttribute("stroke-width", "11.5");

                line.setAttribute("data-dir", dir);

                svg.appendChild(line);
            }

            cell.appendChild(svg);
            row.appendChild(cell);
        }

        table.appendChild(row);
    }
}

function computeDir(a, b) {
    const dx = Math.sign(b.x - a.x);
    const dy = Math.sign(b.y - a.y);

    for (let d = 0; d < 8; d++) {
        if (DIR_VECTORS[d].dx === dx && DIR_VECTORS[d].dy === dy) {
            return d;
        }
    }
    return null;
}


function renderStations(){
    stationData.forEach(stat => {
        const row = table.rows[stat.y];
        const cell = row.cells[stat.x];
        const svg = cell.querySelector("svg");

        const cx = 29; 
        const cy = 29;

        // kör háttér
        const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
        circle.setAttribute("cx", cx);
        circle.setAttribute("cy", cy);
        circle.setAttribute("r", 18);
        circle.setAttribute("fill", "black");
        circle.setAttribute("stroke", "white");
        circle.setAttribute("stroke-width", "2");
        svg.appendChild(circle);
        
        if(stat.type === "?"){
            const textOffsetY = 4;        
            const spacing = 4;           

            // ---- FENT: a kérdőjel ikon ----
            const topIcon = document.createElementNS("http://www.w3.org/2000/svg", "image");
            topIcon.setAttributeNS("http://www.w3.org/1999/xlink", "href", "../img/question.svg");
            topIcon.setAttribute("width", 6.5);
            topIcon.setAttribute("height", 9.2);
            topIcon.setAttribute("x", cx - 6.5 / 2);
            topIcon.setAttribute("y", cy - spacing - 9.2);  // szöveg fölé kerül
            svg.appendChild(topIcon);

            // ---- KÖZÉP: "Deák tér" szöveg ----
            const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
            text.setAttribute("x", cx);
            text.setAttribute("y", cy);
            text.setAttribute("text-anchor", "middle");
            text.setAttribute("font-size", "8.6");
            text.setAttribute("font-family", "FrutigerNextLT, sans-serif");
            text.setAttribute("font-weight", "400");
            text.setAttribute("fill", "white");

            text.setAttribute("dy", "3");   

            text.textContent = "Deák tér";
            svg.appendChild(text);

            // ---- LENT: a nyíl ikon ----
            const bottomIcon = document.createElementNS("http://www.w3.org/2000/svg", "image");
            bottomIcon.setAttributeNS("http://www.w3.org/1999/xlink", "href", "../img/Arrow.svg");
            bottomIcon.setAttribute("width", 30.3);
            bottomIcon.setAttribute("height", 2.8);
            bottomIcon.setAttribute("x", cx - 30.3 / 2);
            bottomIcon.setAttribute("y", cy + spacing);   
            svg.appendChild(bottomIcon);
        }
        else{
            const img = document.createElementNS("http://www.w3.org/2000/svg", "image");
            img.setAttributeNS("http://www.w3.org/1999/xlink", "href", `../img/${stat.type}.svg`);
            img.setAttribute("x", cx - 20 / 2 + 1);
            img.setAttribute("y", cy - 19 / 2 );
            img.setAttribute("width", 19);
            img.setAttribute("height", 19);

            svg.appendChild(img);
        }
        
    });
}

function renderStationStarts(){

    shuffleMetros.forEach(metro => {
        const stat = stationData.filter(s => s.id === metro.start)[0];
        const row = table.rows[stat.y];
        const cell = row.cells[stat.x];

        const svg = cell.querySelector("svg");
        const circle = svg.querySelector("circle");

        circle.setAttribute("fill", metro.color);  
        circle.setAttribute("fill-opacity", 0.5);
    })
}

function highlightMetro(metro){
    const stat = stationData.find(s => s.id === metro.start);
    const row = table.rows[stat.y];
    const cell = row.cells[stat.x];

    const svg = cell.querySelector("svg");
    const circle = svg.querySelector("circle");

    circle.setAttribute("fill", metro.color);  
    circle.setAttribute("fill-opacity", 1);
}

function renderCard(){
    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("width", 244);
    svg.setAttribute("height", 146.4);
    svg.setAttribute("viewBox", `0 0 ${244} ${146.4}`);

    const img = document.createElementNS("http://www.w3.org/2000/svg", "image");
    img.setAttributeNS("http://www.w3.org/1999/xlink", "href", `../img/card.svg`);
    img.setAttribute("x", 0);
    img.setAttribute("y", 0);
    img.setAttribute("width", 244);
    img.setAttribute("height", 146.4);

    svg.appendChild(img);
    cardPlace.appendChild(svg);

}

function renderMetroCard(){
    const cx = 25; 
    const cy = 25;

    const svg = document.createElementNS("http://www.w3.org/2000/svg", "svg");
    svg.setAttribute("width", 50);
    svg.setAttribute("height", 50);
    svg.setAttribute("viewBox", `0 0 ${50} ${50}`);

    // kör háttér
    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", cx);
    circle.setAttribute("cy", cy);
    circle.setAttribute("r", 25);
    circle.setAttribute("fill", `${currentMetro.color}`);
    circle.setAttribute("stroke", "white");
    circle.setAttribute("stroke-width", "2");
    svg.appendChild(circle);

    const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
    text.setAttribute("x", cx);
    text.setAttribute("y", cy);
    text.setAttribute("text-anchor", "middle");
    text.setAttribute("font-size", "20");
    text.setAttribute("font-family", "Rubik, sans-serif");
    text.setAttribute("font-weight", "800");
    text.setAttribute("fill", "white");

    text.setAttribute("dy", "6");  

    text.textContent = `${currentMetro.name}`;
    svg.appendChild(text);

    metroCard.appendChild(svg);
}

function changeMetroCard(){
    const svg = metroCard.querySelector("svg");

    const circle = svg.querySelector("circle");
    circle.setAttribute("fill", currentMetro.color);
    
    const text = svg.querySelector("text");
    text.textContent = `${currentMetro.name}`;


}

function changeCard(card){
    const svg = cardPlace.querySelector("svg");
    const cx = parseFloat(svg.getAttribute("width")) / 2;
    const cy = parseFloat(svg.getAttribute("height")) / 2;

    const circle = document.createElementNS("http://www.w3.org/2000/svg", "circle");
    circle.setAttribute("cx", cx);
    circle.setAttribute("cy", cy);
    circle.setAttribute("r", 30);
    circle.setAttribute("fill", "black");
    circle.setAttribute("stroke", "white");
    circle.setAttribute("stroke-width", "3");
    svg.appendChild(circle);

    if(card === "Joker"){
        const text = document.createElementNS("http://www.w3.org/2000/svg", "text");
            text.setAttribute("x", cx);
            text.setAttribute("y", cy);
            text.setAttribute("text-anchor", "middle");
            text.setAttribute("font-size", "18");
            text.setAttribute("font-family", "Rubik, sans-serif");
            text.setAttribute("font-weight", "600");
            text.setAttribute("fill", "white");

            text.setAttribute("dy", "5");   
            text.textContent = "Joker";
            svg.appendChild(text);
    }else{
        const letterSize = 25;
        const img = document.createElementNS("http://www.w3.org/2000/svg", "image");
        img.setAttributeNS("http://www.w3.org/1999/xlink", "href", `../img/${card}.svg`);
        img.setAttribute("x", cx - letterSize / 2 );
        img.setAttribute("y", cy - letterSize / 2 );
        img.setAttribute("width", letterSize);
        img.setAttribute("height", letterSize);

        svg.appendChild(img);
    }

}

function tick() {
    elapsedSeconds++;

    const minutes = String(Math.floor(elapsedSeconds / 60)).padStart(2, '0');
    const seconds = String(elapsedSeconds % 60).padStart(2, '0');

    document.getElementById("clock").textContent = `${minutes}:${seconds}`;
}

function shuffleArray(array) {
    for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1)); 
        [array[i], array[j]] = [array[j], array[i]];  
    }
    return array;
}

function addToMetroEnds(a, b){
    if(metroEnds.length === 1){
        metroEnds.push({...b});
    }else{
        metroEnds = metroEnds.filter(s => s.id !== a.id);
        metroEnds.push({...b});
    }
}

function checkValidLine(stat){
    if(metros.find(s => stat.x === s.x && stat.y === s.y))
        return false;
    
    const path = getPath(currentStation, stat);
    for (const next of path) {
        if(stationData.filter(s => s.x === next.x && s.y === next.y).length === 1){
            const s = stationData.find(s => s.x === next.x && s.y === next.y);
            if(s.id != currentStation.id && s.id != stat.id){
                return false;
            }
        }
    }
    if(currentStation.x === stat.x || currentStation.y === stat.y){
        return true;
    }
    const difX = Math.abs(currentStation.x - stat.x);
    const difY = Math.abs(currentStation.y - stat.y);
    if(difX === difY){
        return true;
    }
    
    return false;
}

function orientation(a, b, c) {
    const val = (b.y - a.y) * (c.x - b.x) -
                (b.x - a.x) * (c.y - b.y);
    if (val === 0) return 0;
    return (val > 0 ? 1 : 2);
}

function onSegment(a, b, c) {
    return Math.min(a.x, b.x) <= c.x && c.x <= Math.max(a.x, b.x) &&
           Math.min(a.y, b.y) <= c.y && c.y <= Math.max(a.y, b.y);
}

function segmentsIntersect(p1, p2, p3, p4) {
    const o1 = orientation(p1, p2, p3);
    const o2 = orientation(p1, p2, p4);
    const o3 = orientation(p3, p4, p1);
    const o4 = orientation(p3, p4, p2);

    if (o1 !== o2 && o3 !== o4) return true;

    if (o1 === 0 && onSegment(p1, p2, p3)) return true;
    if (o2 === 0 && onSegment(p1, p2, p4)) return true;
    if (o3 === 0 && onSegment(p3, p4, p1)) return true;
    if (o4 === 0 && onSegment(p3, p4, p2)) return true;

    return false;
}

function checkCrossing(newA, newB, existingLines) {

    for (const line of existingLines) {
        const A = line.start;
        const B = line.destination;

        const sharesEndpoint =
            (A.x === newA.x && A.y === newA.y) ||
            (B.x === newA.x && B.y === newA.y) ||
            (A.x === newB.x && A.y === newB.y) ||
            (B.x === newB.x && B.y === newB.y);

        if (sharesEndpoint) continue;

        if (segmentsIntersect(newA, newB, A, B)) {
            return true; 
        }
    }

    return false;
}

function checkSameLine(newA, newB, existingLines){
    for (const line of existingLines) {
        const A = line.start;
        const B = line.destination;

        // szakasz már létezik (A→B)
        if (A.id === newA.id && B.id === newB.id) return true;

        // vagy visszafelé (B→A)
        if (A.id === newB.id && B.id === newA.id) return true;
    }
    return false;
}

function checkCircle(newA){
    if(oneMetroLine.find(s => s.id === newA.id))
        return true;
    return false;
}

function stepTowards(a, b) {
    const dx = Math.sign(b.x - a.x);
    const dy = Math.sign(b.y - a.y);
    return { x: a.x + dx, y: a.y + dy };
}

function getPath(a, b) {
    const path = [];
    let current = { ...a };

    while (current.x !== b.x || current.y !== b.y) {
        current = stepTowards(current, b);
        path.push({ ...current });
    }

    return path;
}

function setLines(a, b){
    let current = { start: a, destination: b};
    metroLines.push(current);
}

function colorLine(cell, dir, color) {
    const svg = cell.querySelector("svg");
    const line = svg.querySelector(`line[data-dir="${dir}"]`);
    if (line) {
        line.setAttribute("stroke", color);
        line.setAttribute("stroke-opacity", "1");
    }
}

function makeMetroLine(a, b, color) {
    let prev = { x: a.x, y: a.y };

    const path = getPath(a, b);
    for (const next of path) {
        const dir = computeDir(prev, next);
        if (dir === null) {
            prev = next;
            continue;
        }

        const prevCell = table.rows[prev.y].cells[prev.x];
        colorLine(prevCell, dir, color);

        const nextCell = table.rows[next.y].cells[next.x];
        const opp = oppositeDir(dir);
        colorLine(nextCell, opp, color);

        prev = next;
    }
}


delegate(table, "click", "td", function (){
    const row = this.parentElement.rowIndex;
    const col = this.cellIndex;

    const stat = stationData.find(stat => stat.x === col && stat.y === row);
    if (!stat) return; 


     if (currentStation === 0) {
        if (!metroEnds.some(s => s.id === stat.id)) {
            return;
        }
        currentStation = stat;
        return;
    }
    if(checkValidLine(stat) && currentStation.id !== stat.id) {
        if (checkCrossing(currentStation, stat, metroLines)) {
            console.log(" Keresztezné a vonalat!");
            return; 
        }

        if (checkSameLine(currentStation, stat, metroLines)) {
            console.log("Már létező szakasz!");
            return;
        }

        if(checkCircle(stat)){
            console.log("Kör/ hurok lesz!");
            return;
        }

        if(stat.type === currentCard || currentCard === "Joker" || stat.type === "?"){
        
        makeMetroLine(currentStation, stat, currentMetro.color);
        addToMetroEnds(currentStation, stat);
        setLines(currentStation, stat);
        oneMetroLine.push(stat);
        nextTurn();
    }}
});

const cardSvg = cardPlace.querySelector("svg");
cardSvg.addEventListener("click", nextTurn);

function delegate(parent, type, selector, handler) {
  parent.addEventListener(type, function (event) {
    const targetElement = event.target.closest(selector);

    if (this.contains(targetElement)) {
      handler.call(targetElement, event);
    }
  });
}

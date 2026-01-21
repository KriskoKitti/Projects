const startButton = document.querySelector("#startButton");
const nameError = document.querySelector("#nameError");

function startGame(){
    const nameInput = document.querySelector("#nameInput").value;
    if(!nameInput){
        nameError.style.display = "block";  // hibaüzenet megjelenítése
        return;
    }
    window.location.href = `pages/game_screen.html?nameInput=${encodeURIComponent(nameInput)}`;
}
nameError.style.display = "none";
startButton.addEventListener("click", startGame);

const rules = document.querySelector("#rulesButton").addEventListener("click", () => {
    window.location.href = "pages/rules.html";
})
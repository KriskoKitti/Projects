<?php
    session_start();
    if (!isset($_SESSION['user_id'])){
        header('location: index.php');
        exit();
    }
    $id = $_SESSION['user_id'];
    include_once('Storage.php');
    $stor = new Storage(new JsonIO('users.json'));
    $user = $stor -> findById($id);

    if(!$user["is_admin"]){
        header('location: index.php');
        exit();
    }

    $statStor = new Storage(new JsonIO('statuses.json'));
    $statuses = $statStor->findAll();

    $voteStor = new Storage(new JsonIO('votes.json'));
    $votes = $voteStor->findAll();

    $catStor = new Storage(new JsonIO('categories.json'));
    $categories = $catStor->findAll();

    $projStor = new Storage(new JsonIO('projects.json'));
    $projects = $projStor->findAll();
    $pending_projects = array_filter($projects, fn($p) => $p["status_id"] === array_find($statuses, fn($s) => $s["status"] === 'pending')["id"]);

    $max_vote = max(array_map( fn($p) => count(array_filter($votes, fn($v) => $v["project_id"] === $p["id"])) , $projects));
    $best_projects = array_filter($projects, fn($p) => $max_vote === count(array_filter($votes, fn($v) => $v["project_id"] === $p["id"])));

    $idsWithVotes = array_map( fn($p) => count(array_filter($votes, fn($v) => $v["project_id"] === $p["id"])) , $projects);
    $projectsWithVotes = array_map(
        function($p) use ($idsWithVotes) {
            $p['votes'] = $idsWithVotes[$p['id']] ?? 0; // ha nincs szavazat
            return $p; // VERY IMPORTANT!
        },
        $projects
    );
    $sorted_projects = [];
    foreach ($categories as $category) {
        $filtered = array_filter( $projectsWithVotes, fn($p) => $p["category"] === $category["id"] );
        usort($filtered, fn($a, $b) => $b['votes'] <=> $a['votes']);
        $filtered = array_slice($filtered, 0, 3);
        $sorted_projects[] = $filtered;
    }
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin oldal</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <h1>Admin oldal</h1>
    <h2>Függőben lévő projektek</h2>
    <ul>
        <?php foreach($pending_projects as $project):?>
            <li>
                <a href="project.php?id=<?= $project['id'] ?>">
                <?= $project["title"] ?>
                </a>
            </li>
        <?php endforeach ?>
    </ul>
    <h2>Top projekt(ek)</h2>
    <ul>
        <?php foreach($best_projects as $project):?>
            <li>
                <a href="project.php?id=<?= $project['id'] ?>">
                <?= $project["title"] ?>
                </a>
            </li>
        <?php endforeach ?>
    </ul>
    <h2>Legjobb projektek kategóriánként</h2>
    <?php foreach($sorted_projects as $categoryProjects): ?>
        <?php if (!empty($categoryProjects)): ?>
        <h3>
            <?php echo array_find($categories, fn($c) => $c["id"] === array_values($categoryProjects)[0]["category"])["text"]; ?>
        </h3>
        <ul>
            <?php foreach($categoryProjects as $project): ?>
                <li>
                    <a href="project.php?id=<?= $project['id'] ?>">
                        <?= $project["title"] ?>
                    </a>
                </li>
            <?php endforeach; ?>
        </ul>
        <?php endif; ?>
    <?php endforeach; ?>
</body>
</html>
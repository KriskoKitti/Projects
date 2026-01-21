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

    $stor = new Storage(new JsonIO('projects.json'));
    $projects = $stor -> findAll();

    $sorted_projects = array_filter($projects, fn($p) => $p["owner"] === $id);
    
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Saját projektek oldal</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <h1>Saját projektek</h1>
    <ul>
        <?php foreach($sorted_projects as $project):?>
            <li>
                <a href="project.php?id=<?= $project['id'] ?>">
                <?= $project["title"] ?>
                </a>
            </li>
        <?php endforeach ?>
    </ul>
</body>
</html>
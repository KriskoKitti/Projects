<?php
    include_once('Storage.php');
    $project_id = isset($_GET['id']) ? (string)$_GET['id'] : null;
    
    $projStor = new Storage(new JsonIO('projects.json'));
    $project = $projStor -> findById($project_id);

    $statStor = new Storage(new JsonIO('statuses.json'));
    $statuses = $statStor -> findAll();

    $catStor = new Storage(new JsonIO('categories.json'));
    $categories = $catStor -> findAll();

    session_start();
    if (isset($_SESSION['user_id'])){
        $user_id = $_SESSION['user_id'];
        $stor = new Storage(new JsonIO('users.json'));
        $user = $stor -> findById($user_id);
    }

    if(!isset($user)){
        if(array_find($statuses, fn($s) => $s["id"] === $project["status_id"])["status"] != "approved"){
            header('location: index.php');
            exit();
        }
    }
    else if(array_find($statuses, fn($s) => $s["id"] === $project["status_id"])["status"] != "approved" && !$user["is_admin"] && $user["id"] != $project["owner"]){
        header('location: index.php');
        exit();
    }

    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        if (isset($_POST['approve']) && $user["is_admin"]){
            $approvedStatus = array_find( $statuses, fn($s) => $s["status"] === "approved" );
            $project['status_id'] = $approvedStatus["id"];
            $project["approved"] = date('Y-m-d H:i');
            $projStor -> update($project['id'], $project);
            header("Location: projects-admin.php");
            exit;
        }
        if (isset($_POST['reject']) && $user["is_admin"]){
            $rejectedStatus = array_find( $statuses, fn($s) => $s["status"] === "rejected" );
            $project['status_id'] = $rejectedStatus["id"];
            $projStor -> update($project['id'], $project);
            header("Location: projects-admin.php");
            exit;
        }
    }
    
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Projekt oldal</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <h1><?php echo $project["title"];?></h1>
    <?php if (!empty($project['image'])): ?>
        <img
            src="<?= $project['image'] ?>"
            alt="<?= $project['title'] ?>"
            class="project-image"
        >
    <?php endif; ?>
    <p><strong>Leírás:</strong> <?php echo $project["description"];?></p>
    <p><strong>Kategória:</strong> <?php echo array_find($categories, fn($c) => $c["id"] === $project["category"])["text"];?></p>
    <p><strong>Irányítószám:</strong> <?php echo $project["postal_code"];?></p>
    <p><strong>Projektet leadó felhasználó:</strong> <?php echo $project["owner"];?></p>
    <p><strong>Leadás dátuma:</strong> <?php echo $project["submitted"];?></p>
    <p><strong>Közzététel dátuma:</strong> <?php echo $project["approved"];?></p>

    <?php if (isset($_SESSION['user_id']) && $user["is_admin"]): ?>
        <form action="project.php?id=<?= $project['id'] ?>" method="POST">
            <button name="approve" type="submit">Elfogadás</button>
            <button name="reject" type="submit">Visszautasítás</button>
        </form>
    <?php endif; ?>
        
</body>
</html>
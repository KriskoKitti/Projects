<?php
    session_start();
    include_once('Storage.php');

    
    $statStor = new Storage(new JsonIO('statuses.json'));
    $statuses = $statStor->findAll();

    $projStor = new Storage(new JsonIO('projects.json'));
    $projects = $projStor->findAll();
    $projects = array_filter($projects, fn($p) => $p["status_id"] === array_find($statuses, fn($s) => $s["status"] === 'approved')["id"]);

    $catStor = new Storage(new JsonIO('categories.json'));
    $categories = $catStor->findAll();

    $voteStor = new Storage(new JsonIO('votes.json'));
    $votes = $voteStor->findAll();
    $userVotes = [];

    
    $user = null;
    if (isset($_SESSION['user_id'])) {
        $id = $_SESSION['user_id'];
        $stor = new Storage(new JsonIO('users.json'));
        $user = $stor->findById($id);

        $userVotes = array_filter( $votes, fn($v) => $v['user_id'] === (string)$_SESSION['user_id'] );
        $votedProjectIds = array_map( fn($v) => $v['project_id'], $userVotes );
    }

    
    if ($_SERVER['REQUEST_METHOD'] === 'POST') {
        if (isset($_POST['login'])) {
            header("Location: login.php");
            exit;
        }
        if (isset($_POST['register'])) {
            header("Location: register.php");
            exit;
        }
        if (isset($_POST['logout'])) {
            session_unset();
            session_destroy();
            header("Location: index.php");
            exit;
        }
        if (isset($_POST['own_projects'])) {
            header("Location: projects-own.php");
            exit;
        }
        if (isset($_POST['new_project'])) {
            header("Location: project-new.php");
            exit;
        }
        if (isset($_POST['admin_project'])) {
            header("Location: projects-admin.php");
            exit;
        }

        
        if (isset($_POST['selection'])) {
            $cat = $_POST['selection'];
            
            $_SESSION['filter_category'] = $cat;
        } else {
            unset($_SESSION['filter_category']);
        }

        if (isset($_POST['project_id'], $_SESSION['user_id'])) {

            $projectId = (string)$_POST['project_id'];
            $selectedProject = array_find($projects, fn($p) => $p["id"] === $projectId);
            $userId = (string)$_SESSION['user_id'];

            $sorted_projects = array_filter($projects, fn($p) => (string)$p["category"] === $selectedProject["category"]);
            $userVotesByCategories = array_filter($sorted_projects, fn($p) => in_array($p["id"], $votedProjectIds));

            $existing = array_find($votes, fn($v) =>
                $v['project_id'] === $projectId && $v['user_id'] === $userId
            );

            if ($existing) {
                $voteStor->delete($existing['id']);
            } else if(count($userVotesByCategories) < 3){
                $voteStor->add([
                    'project_id' => $projectId,
                    'user_id' => $userId
                ]);
            }

            header("Location: index.php");
            exit;
        }
    }

    
    $selectedCategory = $_SESSION['filter_category'] ?? "";

    if ($selectedCategory !== "") {
        $sorted_projects = [
            array_filter($projects, fn($p) => (string)$p["category"] === (string)$selectedCategory)
        ];
    } else {
        $sorted_projects = [];
        foreach ($categories as $category) {
            $filtered = array_filter(
                $projects,
                fn($p) => (string)$p["category"] === (string)$category["id"]
            );
            $sorted_projects[] = $filtered;
        }
    }

    $votesForSortedProjects = [];
    if (isset($_SESSION['user_id'])) {
        if ($selectedCategory !== "") {
            $votesForSortedProjects = count( array_filter( $sorted_projects[0], fn($p) => in_array($p["id"], $votedProjectIds) ));
        } else {
            foreach ($sorted_projects as $categoryId => $projectsInCategory) {
                $votesForSortedProjects[$categoryId] = count( array_filter($projectsInCategory, fn($p) => in_array($p["id"], $votedProjectIds)));
            }
        }
    }

?>

<!DOCTYPE html>
<html lang="hu">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Főoldal</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <form method="POST">
        <div class="main-buttons">
            <?php if ($user): ?>
                <button name="own_projects">Saját projektek</button>
                <button name="new_project">Új projekt</button>
                <button name="admin_project">Admin oldal</button>
            <?php endif; ?>
        </div>

        <div class="top-right">
            <?php if (!$user): ?>
                <button name="login">Bejelentkezés</button>
                <button name="register">Regisztráció</button>
            <?php else: ?>
                <button name="logout">Kijelentkezés</button>
            <?php endif; ?>
        </div>
    </form>

    <form method="POST">
        <select name="selection">
            <option value="" <?= $selectedCategory === "" ? "selected" : "" ?>>-- Kérem válasszon --</option>
            <option value="0" <?= $selectedCategory === "0" ? "selected" : "" ?>>Helyi kis projekt</option>
            <option value="1" <?= $selectedCategory === "1" ? "selected" : "" ?>>Helyi nagy projekt</option>
            <option value="2" <?= $selectedCategory === "2" ? "selected" : "" ?>>Esélyteremtő Budapest</option>
            <option value="3" <?= $selectedCategory === "3" ? "selected" : "" ?>>Zöld Budapest</option>
        </select>
        <button type="submit">Küldés</button>
    </form>

    <?php foreach($sorted_projects as $categoryProjects): ?>
        <?php if (!empty($categoryProjects)): ?>
        <h2>
            <?php echo array_find($categories, fn($c) => $c["id"] === array_values($categoryProjects)[0]["category"])["text"]; ?>
            <?php if (isset($_SESSION['user_id'])): ?>
                ( <?php echo $votesForSortedProjects[array_values($categoryProjects)[0]["category"]] ?> / 3)
             <?php endif; ?>
        </h2>
        <ul>
            <?php foreach($categoryProjects as $project): ?>
                <li>
                    <a href="project.php?id=<?= $project['id'] ?>">
                        <?= $project["title"] ?>
                    </a>
                    <?php if (isset($_SESSION['user_id'])): ?>
                        <?php if (in_array((string)$project['id'], $votedProjectIds)): ?>
                            <span style="color: green; margin-left: 8px;">
                                ✔ Már szavaztál
                            </span>
                        <?php endif; ?>
                        <form method="POST" action="index.php" style="display:inline">
                            <input type="hidden" name="project_id" value="<?= $project['id'] ?>">
                            <button type="submit" name="vote" value="yes">👍</button>
                        </form>
                    <?php endif; ?>
                </li>
            <?php endforeach; ?>
        </ul>
        <?php endif; ?>
    <?php endforeach; ?>
</body>
</html>

<?php
    include_once('Storage.php');

    session_start();
    if (isset($_SESSION['user_id'])){
        $user_id = $_SESSION['user_id'];
        $stor = new Storage(new JsonIO('users.json'));
        $user = $stor -> findById($user_id);
    }else{
        header('location: index.php');
        exit();
    }

    $errors = [];
    if ($_POST){
        $title = $_POST['title'];
        $description = $_POST['description'];
        $category = $_POST['selection'];
        $postal_code = $_POST['postal_code'];
        $image = $_POST['image'];

        if (mb_str_split($title) < 10 ){
            $errors['title'] = 'A felhasználónév minimum 10 karakterből kell állnia!';
        } 

        if (mb_str_split($description) < 150 ){
            $errors['description'] = 'A leírásnak minimum 150 karakterből kell állnia!';
        } 

        $catStor = new Storage(new JsonIO('categories.json'));
        $categories = $catStor -> findAll();
        $categories_2 = array_map(fn($c) => $c["id"], $categories);
        var_dump($categories_2);
        if (!in_array($category, $categories_2))
            $errors["category"] = "Érvénytelen kategória!";

        if (!preg_match('/^1\d{2}[1-9]$/', $postal_code)) {
            $errors["postal_code"] = "Érvénytelen irányítószám!";
        }else{
            $district = (int)substr($postal_code, 1, 2);
            $sub = (int)substr($postal_code, 3, 1);
            if($postal_code !== "1007" && $district < 1 || $district > 23 || $sub < 1 || $sub > 9)
                $errors["postal_code"] = "Érvénytelen irányítószám!";
            
        }
        if(!isset($image)){
            $image = "";
        }
        else if(!filter_var($image, FILTER_VALIDATE_URL)){
            $errors['image'] = "Nem egy valid URL!";
        }

        $stor = new Storage(new JsonIO('projects.json'));
        $projects = $stor -> findAll();
        if (count($errors) == 0){
            $stor -> add([
                "status_id" => 1,
                "title" => $title,
                "description" => $description,
                "category" => $category,
                "postal_code" => $postal_code,
                "image" => $image,
                "owner" => $user_id,
                "submitted" => date('Y-m-d H:i'),
                "approved" => "-"
            ]);
            header('location: index.php');
            exit();
        }
    }
    
    
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Új projekt oldal</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <form action="project-new.php" method="POST">
        <strong>A projekt neve:</strong> <input type="text" class="big-input" name="title" value="<?= $title ?? "" ?>">
        <?= $errors['title'] ?? "" ?>
        <br>
        <strong>A projekt leírása:</strong> <textarea  class="big-textarea" name="description"><?= $description ?? "" ?></textarea >
        <?= $errors['description'] ?? "" ?>
        <br>
        <strong>Kategória: </strong>
        <select name="selection">
            <option value="0" <?= ($category ?? false) === "0" ? "selected" : "" ?>>Helyi kis projekt</option>
            <option value="1" <?= ($category ?? false) === "1" ? "selected" : "" ?>>Helyi nagy projekt</option>
            <option value="2" <?= ($category ?? false) === "2" ? "selected" : "" ?>>Esélyteremtő Budapest</option>
            <option value="3" <?= ($category ?? false) === "3" ? "selected" : "" ?>>Zöld Budapest</option>
        </select>
        <?= $errors['category'] ?? "" ?>
        <br>
        <strong>Az irányítószám:</strong> <input type="postal_code" name="postal_code">
        <?= $errors['postal_code'] ?? "" ?>
        <br>
        <strong>Kép link (opcionális):</strong> <input class="big-input" type="text" name="image">
        <?= $errors['image'] ?? "" ?>
        <br>
        <button type="submit">Leadás</button>
    </form>
</body>
</html>
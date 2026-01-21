<?php
    session_start();
    $errors = [];
    if ($_POST){
        $username = $_POST['username'];
        $password = $_POST['password'];

        if ($username === '')
            $errors['username'] = 'A felhasználónév nem lehet üres!';

        if ($password === '')
            $errors['password'] = 'A jelszó nem lehet üres!';

        if(count($errors) == 0){
            include_once('Storage.php');
            $stor = new Storage(new JsonIO('users.json'));
            $user = $stor -> findOne(['username' => $username]);
            if ($user === null || !password_verify($password, $user['password'])){
                $errors['login'] = 'Hibás felhasználónév vagy jelszó!';
            } else {
                $_SESSION['user_id'] = $user['id'];
                header('location: index.php');
                exit();
            }
        }
    }
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Bejelentkezés</title>
    <link rel="stylesheet" href="index.css">
</head>
<body>
    <form action="login.php" method="POST">
        Felhasználónév: <input type="text" name="username" value="<?= $username ?? "" ?>">
        <?= $errors['username'] ?? "" ?>
        <br>
        Jelszó: <input type="password" name="password">
        <?= $errors['password'] ?? "" ?>
        <br>
        <button type="submit">Bejelentkezés</button>
    </form>
</body>
</html>
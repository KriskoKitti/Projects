<?php
    $errors = [];
    if ($_POST){
        $username = $_POST['username'];
        $email = $_POST['email'];
        $password1 = $_POST['password1'];
        $password2 = $_POST['password2'];

        include_once('Storage.php');
        $stor = new Storage(new JsonIO('users.json'));

        if ($username === ""){
            $errors['username'] = 'A felhasználónév nem lehet üres!';
        } else {
            $user = $stor -> findOne(['username' => $username]);
            if ($user !== null){
                $errors['username'] = 'A felhasználónév már foglalt!';
            }
            if(array_any(mb_str_split($username), fn($n) => $n === " ")){
                $errors['username'] = 'A felhasználónév nem tartalmazhat szóközt!';
            }
        }

        if ($email === ""){
            $errors['email'] = 'Az e-mail nem lehet üres!';
        } else {
            if (!filter_var($email, FILTER_VALIDATE_EMAIL)){
                $errors['email'] = "Nem egy valid e-mail cím!";
            }
        }
        $lowerCase = ["a", "á", "b", "c", "d", "e", "é", "f", "g", "h", "i", "í", "j", "k", "l", "m", "n", "o", "ó", "ö", "ő", "p", "q", "r", "s", "t", "u", "ú", "ü", "ű", "v", "w", "x", "y", "z"];
        $upperCase = ["A", "Á", "B", "C", "D", "E", "É", "F", "G", "H", "I", "Í", "J", "K", "L", "M", "N", "O", "Ó", "Ö", "Ő", "P", "Q", "R", "S", "T", "U", "Ú", "Ü", "Ű", "V", "W", "X", "Y", "Z"];
        if ($password1 === ""){
            $errors['password1'] = 'A jelszó nem lehet üres!';
        }else{
            if(count(mb_str_split($password1)) < 8)
                $errors['password1'] = 'A jelszónak legalaább 8 karakternek kelle lennie!';

            if(count(array_intersect($lowerCase, mb_str_split($password1))) === 0 || count(array_intersect($upperCase, mb_str_split($password1))) === 0 || !array_any(mb_str_split($password1), fn($n) => is_numeric($n)))
                $errors['password1'] = 'A jelszónak tartalmaznia kell kisbetűt, nagybetűt és számjegyet!';
        }

        if ($password1 !== $password2)
            $errors['password2'] = 'A jelszavak nem egyeznek meg!';

        if (count($errors) == 0 || $username === "admin"){
            $stor -> add([
                "username" => $username,
                "email" => $email,
                "password" => password_hash($password1, PASSWORD_DEFAULT),
                "is_admin" => false
            ]);
            header('location: login.php');
            exit();
        }
    }
?>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
</head>
<body>
    <form action="register.php" method="POST">
        Felhasználónév: <input type="text" name="username" value="<?= $username ?? "" ?>">
        <?= $errors['username'] ?? "" ?>
        <br>
        E-mail: <input type="text" name="email" value="<?= $email ?? "" ?>">
        <?= $errors['email'] ?? "" ?>
        <br>
        Jelszó: <input type="password" name="password1">
        <?= $errors['password1'] ?? "" ?>
        <br>
        Jelszó mégegyszer: <input type="password" name="password2">
        <?= $errors['password2'] ?? "" ?>
        <br>
        <button type="submit">Regisztráció</button>
    </form>
</body>
</html>
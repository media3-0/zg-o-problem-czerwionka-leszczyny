<?php

//upload zgłoszenia

require_once('database.php');

$pdo = setupPDO();

$stmt = $pdo -> prepare("INSERT INTO zgloszenia (ident, imie, nazwisko, description, lat, lng)	VALUES(
				:ident,
				:imie,
				:nazwisko,
				:description,
				:lat,
				:lng
				)");

if(isset($_POST['ident'])){
    $stmt->bindValue(':ident', $_POST['ident'], PDO::PARAM_STR);
}else{
    $stmt->bindValue(':ident', 0, PDO::PARAM_STR);
}

if(isset($_POST['imie'])){
    $stmt->bindValue(':imie', $_POST['imie'], PDO::PARAM_STR);
}else{
    $stmt->bindValue(':imie', NULL, PDO::PARAM_STR);
}

if(isset($_POST['nazwisko'])){
    $stmt->bindValue(':nazwisko', $_POST['nazwisko'], PDO::PARAM_STR);
}else{
    $stmt->bindValue(':nazwisko', NULL, PDO::PARAM_STR);
}

if(isset($_POST['description'])){
    $stmt->bindValue(':description', $_POST['description'], PDO::PARAM_STR);
}else{
    $stmt->bindValue(':description', NULL, PDO::PARAM_STR);
}
if(isset($_POST['pos_lat'])){
    $stmt->bindValue(':lat', $_POST['pos_lat'], PDO::PARAM_STR);
}else{
    $stmt->bindValue(':lat', NULL, PDO::PARAM_STR);
    die('Error: brak lokacji');
}

if(isset($_POST['pos_lng'])){
    $stmt->bindValue(':lng', $_POST['pos_lng'], PDO::PARAM_STR);
}else{
    $stmt->bindValue(':lng', NULL, PDO::PARAM_STR);
    die('Error: brak lokacji');
}

if(isset($_FILES["file"])){
    $allowedExts = array("gif", "jpeg", "jpg", "png");
    $temp = explode(".", $_FILES["file"]["name"]);
    $extension = strtolower(end($temp));

    if (
        /*(($_FILES["file"]["type"] == "image/gif")
            || ($_FILES["file"]["type"] == "image/jpeg")
            || ($_FILES["file"]["type"] == "image/jpg")
            || ($_FILES["file"]["type"] == "image/pjpeg")
            || ($_FILES["file"]["type"] == "image/x-png")
            || ($_FILES["file"]["type"] == "image/png"))
        //&& ($_FILES["file"]["size"] < 20000)
        && */
    in_array($extension, $allowedExts)) {
        if ($_FILES["file"]["error"] > 0) {
            die(" Error: " . $_FILES["file"]["error"]);
        }
    } else {
        die("Error: zly format pliku");
    }
}else{
    die('Error: Brak pliku');
}

$stmt->execute();
$id = $pdo->lastInsertId();
if($id == 0)
    die('Error: blad podczas dodawania do bazy');
move_uploaded_file($_FILES["file"]["tmp_name"],"images/".$id.".".$extension);




echo 'ok';
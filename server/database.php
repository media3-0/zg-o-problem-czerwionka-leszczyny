<?php

//baza danych

function setupPDO(){
    try{
        $pdo = new PDO('mysql:host=localhost;dbname=zgloszenia', 'root', '');
    }catch (PDOException $e){
        die('Połączenie z bazą danych nie mogło zostać utworzone: '.$e->getMessage());
    }
    $pdo -> setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    return $pdo;
}
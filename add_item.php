<?php
file_put_contents("php_error_log.txt", "Reached script\n", FILE_APPEND);
file_put_contents("php_error_log.txt", print_r($_POST, true), FILE_APPEND);

ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

header('Content-Type: application/json');

$host = "localhost";
$user = "root";
$password = "";
$db = "preloved_db1";

$conn = new mysqli($host, $user, $password, $db);
if ($conn->connect_error) {
    echo json_encode(["status" => "error", "message" => "DB Connection failed: " . $conn->connect_error]);
    exit;
}

$title = $_POST['title'] ?? $_GET['title'] ?? '';
$description = $_POST['description'] ?? $_GET['description'] ?? '';
$price = $_POST['price'] ?? $_GET['price'] ?? '';
$item_image = $_POST['item_image'] ?? $_GET['item_image'] ?? '';
$user_id = $_POST['user_id'] ?? $_GET['user_id'] ?? '';

if (empty($title) || empty($price) || empty($user_id)) {
    echo json_encode(["status" => "error", "message" => "Missing required fields."]);
    exit;
}

$stmt = $conn->prepare("INSERT INTO items (user_id, title, description, price, item_image) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("issds", $user_id, $title, $description, $price, $item_image);

if ($stmt->execute()) {
    echo json_encode(["status" => "success", "message" => "Item added."]);
} else {
    echo json_encode(["status" => "error", "message" => "Execution failed: " . $stmt->error]);
}

$stmt->close();
$conn->close();

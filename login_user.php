<?php
header('Content-Type: application/json');
ini_set('display_errors', 1);
ini_set('display_startup_errors', 1);
error_reporting(E_ALL);

// Connect to MySQL
$conn = new mysqli("localhost", "root", "", "preloved_db1");

if ($conn->connect_error) {
    echo json_encode(["status" => "db_connection_failed"]);
    exit();
}

// Get POST data
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// Basic validation
if (empty($email) || empty($password)) {
    echo json_encode(["status" => "missing_fields"]);
    exit();
}

// Prepare and execute query
$sql = "SELECT id, username, password FROM users WHERE email = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("s", $email);
$stmt->execute();
$stmt->store_result();

if ($stmt->num_rows > 0) {
    $stmt->bind_result($id, $username, $storedPassword);
    $stmt->fetch();

    if ($password === $storedPassword) {
        echo json_encode(["status" => "success", "user_id" => $id, "username" => $username]);
    } else {
        echo json_encode(["status" => "invalid_password"]);
    }
} else {
    echo json_encode(["status" => "user_not_found"]);
}

// Assume you already verified username and password
$user_id = $row['id']; // fetched from users table after successful login
$username = $row['username'];

echo json_encode([
    "status" => "success",
    "user_id" => $user_id,
    "username" => $username
]);

?>

<?php 
$host = "localhost";
$dbname = "preloved_db1";
$username = "root";
$password = "";

$conn = new mysqli($host, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["status" => "error", "message" => "Database connection failed."]));
}

$user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
$title = isset($_POST['item_title']) ? trim($_POST['item_title']) : '';
$quantity_change = isset($_POST['quantity']) ? intval($_POST['quantity']) : 0;

if ($user_id <= 0 || empty($title) || $quantity_change == 0) {
    echo json_encode(["status" => "error", "message" => "Invalid input."]);
    exit;
}

// Find item_id by title
$stmt = $conn->prepare("SELECT id FROM items WHERE title = ?");
$stmt->bind_param("s", $title);
$stmt->execute();
$result = $stmt->get_result();

if (!$row = $result->fetch_assoc()) {
    echo json_encode(["status" => "error", "message" => "Item not found."]);
    exit;
}

$item_id = $row['id'];

// Check if user already has the item in cart
$stmt = $conn->prepare("SELECT quantity FROM user_cart WHERE user_id = ? AND item_id = ?");
$stmt->bind_param("ii", $user_id, $item_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $new_quantity = max(0, $row['quantity'] + $quantity_change);

    $stmt = $conn->prepare("UPDATE user_cart SET quantity = ? WHERE user_id = ? AND item_id = ?");
    $stmt->bind_param("iii", $new_quantity, $user_id, $item_id);
    $stmt->execute();
} else if ($quantity_change > 0) {
    $stmt = $conn->prepare("INSERT INTO user_cart (user_id, item_id, quantity) VALUES (?, ?, ?)");
    $stmt->bind_param("iii", $user_id, $item_id, $quantity_change);
    $stmt->execute();
}

echo json_encode(["status" => "success", "message" => "Cart updated."]);
$conn->close();
?> 
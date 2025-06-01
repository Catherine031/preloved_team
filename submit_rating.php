<?php
$host = "localhost";
$dbname = "preloved_db1";
$username = "root";
$password = "";

$conn = new mysqli($host, $username, $password, $dbname);
if ($conn->connect_error) {
    die(json_encode(["success" => false, "message" => "Database connection failed."]));
}

$user_id = isset($_POST['user_id']) ? intval($_POST['user_id']) : 0;
$title = isset($_POST['title']) ? trim($_POST['title']) : '';
$rating_value = isset($_POST['rating_value']) ? floatval($_POST['rating_value']) : -1;

// Validate input
if ($user_id <= 0 || $rating_value < 0 || $rating_value > 5 || empty($title)) {
    echo json_encode(["success" => false, "message" => "Invalid input."]);
    exit;
}

// Get item ID by title
$stmt = $conn->prepare("SELECT id FROM items WHERE title = ?");
$stmt->bind_param("s", $title);
$stmt->execute();
$result = $stmt->get_result();

if (!$row = $result->fetch_assoc()) {
    echo json_encode(["success" => false, "message" => "Item not found."]);
    exit;
}

$item_id = $row['id'];

// Check if user already rated this item
$stmt = $conn->prepare("SELECT id FROM ratings WHERE user_id = ? AND item_id = ?");
$stmt->bind_param("ii", $user_id, $item_id);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows > 0) {
    echo json_encode(["success" => false, "message" => "User has already rated this item."]);
    exit;
}

// Insert the rating
$stmt = $conn->prepare("INSERT INTO ratings (user_id, item_id, rating_value) VALUES (?, ?, ?)");
$stmt->bind_param("iid", $user_id, $item_id, $rating_value);
$stmt->execute();

// Recalculate average and count
$stmt = $conn->prepare("SELECT COUNT(*), AVG(rating_value) FROM ratings WHERE item_id = ?");
$stmt->bind_param("i", $item_id);
$stmt->execute();
$stmt->bind_result($count, $avg);
$stmt->fetch();
$stmt->close();

// Update items table
$stmt = $conn->prepare("UPDATE items SET rating_count = ?, average_rating = ? WHERE id = ?");
$stmt->bind_param("idi", $count, $avg, $item_id);
$stmt->execute();

echo json_encode(["success" => true, "message" => "Rating submitted successfully."]);

$conn->close();
?>

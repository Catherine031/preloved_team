<?php 
$host = "localhost";
$dbname = "preloved_db1";
$username = "root";
$password = "";

$conn = new mysqli($host, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

if (!isset($_GET['title'])) {
    echo json_encode(["success" => false, "message" => "No title provided."]);
    exit;
}

$title = $_GET['title'];
$user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

// Query: fetch item details (including stored rating_count and average_rating)
$sql = "SELECT 
            i.id AS item_id,
            i.title,
            i.description,
            i.price,
            i.item_image,
            u.username,
            i.rating_count,
            i.average_rating,
            (
                SELECT rating_value 
                FROM ratings 
                WHERE item_id = i.id AND user_id = ?
                LIMIT 1
            ) AS user_rating
        FROM items i
        JOIN users u ON i.user_id = u.id
        WHERE i.title = ?
        LIMIT 1";

$stmt = $conn->prepare($sql);
$stmt->bind_param("is", $user_id, $title);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    echo json_encode(["success" => true, "item" => $row]);
} else {
    echo json_encode(["success" => false, "message" => "Item not found."]);
}

$conn->close();
?>

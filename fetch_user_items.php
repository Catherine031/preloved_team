<?php
$host = "localhost";
$dbname = "preloved_db1";
$username = "root"; // default for XAMPP
$password = "";     // default for XAMPP

$conn = new mysqli($host, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$user_id = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;

if ($user_id === 0) {
    http_response_code(400);
    echo json_encode(["error" => "Missing or invalid user ID"]);
    exit;
}

$sql = "
    SELECT items.title, items.price, items.item_image, items.description, users.username
    FROM items
    JOIN users ON items.user_id = users.id
    WHERE users.id = $user_id
";

$result = $conn->query($sql);
$items = [];

while ($row = $result->fetch_assoc()) {
    $row['item_image'] = "http://10.0.2.2/preloved/" . $row['item_image']; // Adjust for emulator access
    $items[] = $row;
}

header('Content-Type: application/json');
echo json_encode($items);
$conn->close();
?>

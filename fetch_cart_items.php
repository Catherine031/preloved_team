<?php
$host = "localhost";
$dbname = "preloved_db1";
$username = "root";
$password = "";

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
    SELECT 
        uc.user_id,
        u.username,
        i.title AS item_title,
        uc.quantity,
        i.price,
        CONCAT('http://10.0.2.2/preloved/', i.item_image) AS item_image,
        i.description
    FROM user_cart uc
    JOIN users u ON uc.user_id = u.id
    JOIN items i ON uc.item_id = i.id
    WHERE uc.user_id = ? AND uc.quantity > 0
";

$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

$items = [];
while ($row = $result->fetch_assoc()) {
    $items[] = $row;
}

header('Content-Type: application/json');
echo json_encode($items);

$stmt->close();
$conn->close();
?>
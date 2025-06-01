<?php
$host = "localhost";
$dbname = "preloved_db1";
$username = "root"; // default for XAMPP
$password = "";     // default for XAMPP

$conn = new mysqli($host, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$page = isset($_GET['page']) ? intval($_GET['page']) : 0;
$query = isset($_GET['query']) ? $conn->real_escape_string($_GET['query']) : '';

$itemsPerPage = 4;
$offset = $page * $itemsPerPage;

$sql = "
    SELECT items.title, items.price, items.item_image, items.description, users.username
    FROM items
    JOIN users ON items.user_id = users.id
    WHERE items.title LIKE '%$query%' OR items.description LIKE '%$query%'
    LIMIT $itemsPerPage OFFSET $offset
";

$result = $conn->query($sql);
$items = [];

while ($row = $result->fetch_assoc()) {
    $row['item_image'] = "http://10.0.2.2/preloved/" . $row['item_image']; // Adjust for your XAMPP path
    $items[] = $row;
}

header('Content-Type: application/json');
echo json_encode($items);
$conn->close();
?>

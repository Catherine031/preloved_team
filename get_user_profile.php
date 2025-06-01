<?php
$conn = new mysqli("localhost", "root", "", "preloved_db1");

$user_id = $_GET['user_id'];

$sql = "SELECT username, email, profile_image FROM users WHERE id = ?";
$stmt = $conn->prepare($sql);
$stmt->bind_param("i", $user_id);
$stmt->execute();
$result = $stmt->get_result();

if ($row = $result->fetch_assoc()) {
    $row['profile_image'] = "http://10.0.2.2/preloved/" . $row['profile_image'];
    echo json_encode($row);
} else {
    echo json_encode(["error" => "User not found"]);
}
?>

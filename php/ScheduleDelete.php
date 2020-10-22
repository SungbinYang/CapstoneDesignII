<?php
	$con = mysqli_connect("localhost", "ysb0522", "didtjdqls1223!", "ysb0522");

	$userID = $_POST["userID"];
	$courseID = $_POST["courseID"];

	$statement = mysqli_prepare($con, "DELETE FROM SCHEDULE WHERE userID = '$userID' AND courseID = '$courseID'");
	mysqli_stmt_bind_param($statement, "si", $userID, $courseID);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;
	
	echo json_encode($response);
?>
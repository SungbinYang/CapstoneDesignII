<?php
	$con = mysqli_connect("localhost", "ysb0522", "didtjdqls1223!", "ysb0522");

	$userID = $_POST["userID"];
	$userPassword = $_POST["userPassword"];
	$userGender = $_POST["userGender"];
	$userMajor = $_POST["userMajor"];
	$userEmail = $_POST["userEmail"];

	$checkedPassword = password_hash($userPassword, PASSWORD_DEFAULT);
	$statement = mysqli_prepare($con, "INSERT INTO USERS VALUES(?, ?, ?, ?, ?)");
	mysqli_stmt_bind_param($statement, "sssss", $userID, $checkedPassword, $userGender, $userMajor, $userEmail);
	mysqli_stmt_execute($statement);

	$response = array();
	$response["success"] = true;

	echo json_encode($response);
?>
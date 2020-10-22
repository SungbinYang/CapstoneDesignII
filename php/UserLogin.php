<?php
	$con = mysqli_connect("localhost", "ysb0522", "didtjdqls1223!", "ysb0522");

	$userID = $_POST["userID"];
	$userPassword = $_POST["userPassword"];

	$statement = mysqli_prepare($con, "SELECT * FROM USERS WHERE userID = ?");
	mysqli_stmt_bind_param($statement, "s", $userID);
	mysqli_stmt_execute($statement);

	mysqli_stmt_store_result($statement);
	mysqli_stmt_bind_result($statement, $userID, $ckeckedPassword, $userGender, $userMajor, $userEmail);

	$response = array();
	$response["success"] = false;

	while (mysqli_stmt_fetch($statement)) {
		if(password_verify($userPassword, $ckeckedPassword)) {
			$response["success"] = true;
			$response["userID"] = $userID;
		}
	}

	echo json_encode($response);
?>
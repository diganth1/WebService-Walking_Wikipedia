<?php
require_once('authenticationInclude.php');

//Now that we're logged in or out appropriately, let's check if we need to redirect them to the login page:
if(!array_key_exists('sessionID', $_SESSION)){
	header("Location: $loginPageLocation");
	die();
}
?>
Find Shortest Path Between Pages (in parallel!)<br>
<form action="getShortestPathParallelResult.php" method="post">
<p>Enter first page: <input type="text" name="a" /></p>
<p>Enter second page: <input type="text" name="b" /></p>
<p><input type="submit" value="Submit" /></p>
</form>
<br>
<a href="<?php echo $homePage; ?>">Home</a>

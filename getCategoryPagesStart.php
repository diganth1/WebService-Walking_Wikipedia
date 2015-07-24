<?php
require_once('authenticationInclude.php');

//Now that we're logged in or out appropriately, let's check if we need to redirect them to the login page:
if(!array_key_exists('sessionID', $_SESSION)){
	header("Location: $loginPageLocation");
	die();
}
?>
Get Category's Pages<br>
<form action="getCategoryPagesResult.php" method="post">
<p>Enter category: <input type="text" name="a" /></p>
<p><input type="submit" value="Submit" /></p>
</form>
<br>
<a href="<?php echo $homePage; ?>">Home</a>
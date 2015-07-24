 <?php 
require_once('authenticationInclude.php');

//If they're logged in, redirect them to the homepage
if(array_key_exists('sessionID', $_SESSION)){
	header("Location: $homePage");
	die();
}

/* FOR OTHER PAGES:
//Now that we're logged in or out appropriately, let's check if we need to redirect them to the login page:
if(!array_key_exists('sessionID', $_SESSION)){
	header("Location: $loginPageLocation");
	die();
}
*/
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<script type="text/javascript" src="sha256.js"></script>
<script type="text/javascript">
var serverSalt = "fdSfdBQo";
function submitLogin(){
	salt = document.getElementById("salt").value;
	pf = document.getElementById("loginPasswordText");
	document.getElementById("loginPassword").value = SHA256(SHA256(pf.value+serverSalt)+salt);
	document.getElementById("loginForm").submit();
}
function submitRegister(){
	pf = document.getElementById("registerPasswordText");
	document.getElementById("registerPassword").value = SHA256(pf.value+serverSalt);
	document.getElementById("registerForm").submit();
}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Wiki-Walking - Login</title>
<link href="style.css" rel="stylesheet" type="text/css" />
<link rel="icon" href="images/logo.jpg" />
</head>
<body bgcolor="#808000">
	<img style="display: block; margin-left: auto; margin-right: auto" src="images/logo.jpg" alt="LOGO" width="450" height="275px">
	<?php if($loginMessage != null) echo "<h3>".$loginMessage."</h3><br/>";?>
	<div style="width: 700px; display: block; margin-left: auto; margin-right: auto;">
		<table border="0" style="display: block; margin-left: auto; margin-right: auto; vertical-align: top"><tr><td>
			<form action="" method="post" id="loginForm">
				<input type="hidden" name="salt" id="salt" value="<?php echo $_SESSION['salt']; ?>"/>
				<input type="hidden" name="password" id="loginPassword"/>
				<table border="0"> 
					<tr><td colspan=2><h1>Login</h1></td></tr> 
					<tr><td><font size="5">Username:</td><td><input type="text" name="username" size="30" maxlength="40"/></font></td></tr>
					<tr><td><font size="5">Password:</td><td><input type="password" id="loginPasswordText" size="30" maxlength="50"/></font></td></tr>
					<tr><td colspan="3" align="right"><font size="5"><input type="submit" name="login" value="Login" onclick="submitLogin()"/></font></td></tr>
					<tr><td colspan="3" align="right"><font size="5">&nbsp;</font></td></tr>
				</table>
			</form>
		</td><td>
			<form action="" method="post" id="registerForm">
				<input type="hidden" name="password" id="registerPassword"/>
				<table border="0">
					<tr><td colspan=2><h1>Register</h1></td></tr> 
					<tr><td><font size="5">Username:</td><td><input type="text" name="username" size="30" maxlength="40"/></font></td></tr>
					<tr><td><font size="5">Password:</td><td><input type="password" id="registerPasswordText" size="30" maxlength="50"/></font></td></tr>
					<tr><td><font size="5">Email:</td><td><input type="text" name="email" size="30" maxlength="40"/></font></td></tr>
					<tr><td colspan="3" align="right"><font size="5"><input type="submit" name="register" value="Register" onclick="submitRegister()"/></font></td></tr>
				</table>
			</form>
		</td></tr></table>
	</div>
</body>
</html>
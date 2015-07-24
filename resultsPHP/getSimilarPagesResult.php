<?php
require_once("authenticationInclude.php");

//Now that we're logged in or out appropriately, let's check if we need to redirect them to the login page:
if(!array_key_exists('sessionID', $_SESSION)){
	header("Location: $loginPageLocation");
	die();
}

ini_set("soap.wsdl_cache_enabled", "0");

//print "Entering...<br>";

//print "WSDL string declared...<br>";

try{
    //echo "Attempting to create SOAP client...<br>";

    //$client = new SoapClient($wsdl, array('exceptions' => 0));
    $client = new SoapClient($wsdl);

    //echo "Client created successfully!<br>";
}catch (Exception $e){echo 'Caught exception: ',$e->getMessage(),"\n";}

//print "Exiting try/catch...<br>";

//$params = array('a'=>'2','b'=>'5');
//$result = $client->add($params);
$result = $client->getSimilarPages($_POST['a'], $_SESSION['sessionID']);

if(is_soap_fault($result))
{
    trigger_error("SOAP Fault: (faultcode: {$result->faultcode},faultstring:{$result->faultstring})", E_USER_ERROR);
}
echo "<br>".nl2br($result)."<br><br>";
//echo "Exiting...<br>";
?>
<a href="getSimilarPagesStart.php">New Query</a>
<br>
<a href="<?php echo $homePage; ?>">Home</a>
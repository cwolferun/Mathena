<?php
/* this program executes a simple forward difference method
to return approx derivative at a point*/


$newstring = $_POST["package"];
$delta = .00001;
$func;
$var1;
$var2;
$var3;
$var4;
$var5;

$pointA;
$pointB;
$answer;
$func = strtok($newstring, " \n");
$var1 = strtok(" \n");
$var2 = strtok($var1,"=");
$var3= strtok(" \n");
$var4 = $var3+$delta;
$var5 = $var2.'='.$var4;
//echo "$var5 <br/>";
$pointA = exec("solveEq $func $var1");
$pointB = exec("solveEq $func $var5");
$answer = ($pointB-$pointA)/$delta;

echo "$answer";

?>

<?php
/*
this program uses simpson's rule 
to integrate a 3 dimesional surface between 4 end points
*/

/* incoming string will look something  like: "5x^2+2x+3y^2-4y x=1 x=3 y=2 y=5"*/ 
$inputString = $_POST["package"];
//$inputString = "5x^2+2x+3y^2-4y x=1 x=3 y=2 y=5"; 
$func = strtok($inputString," ");	//seperate by spaces
$var1=strtok(" ");
$var2=strtok(" ");
$var3=strtok(" ");
$var4=strtok("\n");

$firstVar=strtok($var1,"=");	//strip the values from the right of the equal sign
$a=strtok("\n");		//a,b,c,d are the 4 endpoints

$var5=strtok($var2,"=");
$b=strtok("\n");

$secVar=strtok($var3,"=");
$c=strtok("\n");

$var6=strtok($var4,"=");
$d=strtok("\n");

$range1 = ($b+$a)/2;
$range2 = ($c+$d)/2;


$call1=exec("solveEq $func $firstVar=$range1 $secVar=$range2");
$call2=exec("solveEq $func $firstVar=$range1 $var4");
$call3=exec("solveEq $func $firstVar=$range1 $var3");
$call4=exec("solveEq $func $var2 $secVar=$range2");
$call5=exec("solveEq $func $var2 $var4");
$call6=exec("solveEq $func $var2 $var3");
$call7=exec("solveEq $func $var1 $secVar=$range2");
$call8=exec("solveEq $func $var1 $var4");
$call9=exec("solveEq $func $var1 $var3");


$answer = (16 * $call1 + 4 * $call2 + 4 * $call3 + 4 * $call4 + $call5
	+ $call6 + 4 * $call7 + $call8 + $call9)*($b - $a)*($d - $c) / 36;
echo "$answer";

?>

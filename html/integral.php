<?php

/*this entire file employs composite simpsons rule 
to integrate a function and its endpoints*/

$inputString = $_POST["package"];

$func = strtok($inputString," ");
$pointA = strtok(" \n");
$pointB = strtok(" \n");

$var = strtok($pointA,"=");
$a = strtok(" \n");

strtok($pointB,"=");
$b= strtok(" \n");


$tot=0;
$n=8;
$split = ($b-$a)/(2*$n);
for($i=1; $i <= 2*$n; $i++)
{
	$begin = $a+$split*($i-1);
	$end = $a+$split*$i;
	$midPoint = ($end+$begin)/2;
	
	$first = exec("solveEq $func $var=$begin");
	$middle = exec("solveEq $func $var=$midPoint");
	$last = exec("solveEq $func $var=$end");

	$tot += ($end-$begin)/3*($first + 4*$middle + $last);

}
$tot /=2;
echo $tot;
?>

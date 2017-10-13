// eqautionTest.cpp : Defines the entry point for the console application.


/*once compiled, this program takes a function and variable points
and outputs the value of the funcion at that point(s)*/

#include <iostream>
#include <string>
#include <string.h>
#include <vector>
#include <map>
#include <math.h>

using namespace std;
double evalside(string s, map<string,string> vars);
double getlastnum(string s, map<string, string> vars);
string inputmultiply(string equation);
string evaluate_all(string equation, map<string, string> vars);


int main(int argc, char** argv)
{
	string varname;
	string varvalue;
	map<string, string> variables;


	//prints out original equation
	for (int i = 2; i < argc; i++) 
	{
		string ths = argv[i];
		for (unsigned int t = 0; t < ths.length(); t++)
		{
			if (ths[t] == '=')
			{
				//parse the variables into a map with their matching value
				varname = ths.substr(0, t);
				varvalue = ths.substr(t + 1);
				variables[varname]=varvalue;

			} 
		}
	}
	string input_eq = argv[1];

	string eq = "(" + input_eq + ")";	//needed to process equation as a node

	string var1 = "xaz";
	eq = inputmultiply(eq);				//inputs explicit multiplication symbol between ')' and '('
	
	bool end_of_eq = 0;
	string temp_eq;


	for (unsigned int i = 0; i < eq.length(); i++){
		/*scans string for closing parenth, then scans backward for open parenth. This will be solved*/
		if (eq[i] == ')') {
			int end = i;
			for (int j = i; j >= 0; j--) {
				if (eq[j] == '(') {
					string temp = eq.substr(j+1, i - j-1 );		//takes number sentence without its parenths

					string valu = evaluate_all(temp,variables);

					// takes solve for value and puts it back in place of equation that was solved
					if (i == eq.length()-1) {
						 temp_eq = eq.substr(0, j) + valu;
					}
					else {
						 temp_eq = eq.substr(0, j) + valu + eq.substr(i + 1);
					}
					//cout << "new equation is " << temp_eq << endl;

					eq = temp_eq;
					break;
				}
			}
			i = 0;		//restarts process as the original equation has be overwritten
		}

}
	cout << eq << endl;
	int pause;
	cin >> pause;
    return 0;
}

		/*this function handle one side of an operator at a time. however implied multiplication such as 2xy
		are handled at once*/
double evalside(string hand, map<string, string> mymap) {



	vector<string> values;

	vector<double> truvalues;
	double val = 1.0;
	map<string,string>::iterator it;
		double temp;
		string value;


	string potvar = "";
	bool readingnums = 1;
	bool readingvar = 0;
	for (int k = 0; k < hand.length(); k++) {
		if (hand[k] == '-') { val = -1;  continue; }	//if the first character is '-' then the number is negative and that character needs to be skipped

		if (isalpha(hand[k])) {
			if (readingnums) {
				readingnums = 0;
				readingvar = 1;
				if (!(potvar.empty())) {
					values.push_back(potvar);
					potvar.clear();
				}
			}

			potvar += hand[k];
		}
		else {
			if (readingvar) {
				readingnums = 1;
				readingvar = 0;
				if (!(potvar.empty())) {

					values.push_back(potvar);
					potvar.clear();
				}
			}
			potvar += hand[k];
		}
		if (k == hand.length() - 1) {
			values.push_back(potvar);
			potvar.clear();
		}

	}
	
	for (int i = 0; i < values.size(); i++) {
		string pointingTo = values[i];
		it = mymap.find(pointingTo);
		if (it != mymap.end()) {
			value = mymap[pointingTo];
		temp = atof(value.c_str());

		}
		else{
			temp = atof(pointingTo.c_str());
		}

		val *= temp;

	}


	return val;

}

/*this function is needed in the case of exponentials such as 2x4^3 so the only lefthand 
value being operated on is the last variable or number. in the case above, only 4 should be cubed*/
double getlastnum(string st, map<string, string> mymap) {
	//cout << "get last num is running " << endl;

	vector<string> values2;
	map<string, string>::iterator it;
	double temp;
	string value;

	string potvar = "";
	bool readingnums = 1;
	bool readingvar = 0;
	for (int k = 0; k < st.length(); k++) {
		if (isalpha(st[k])) {
			if (readingnums) {
				readingnums = 0;
				readingvar = 1;
				if (!(potvar.empty())) {
					values2.push_back(potvar);
					potvar.clear();
				}
			}

			potvar += st[k];
		}
		else {
			if (readingvar) {
				readingnums = 1;
				readingvar = 0;
				if (!(potvar.empty())) {

					values2.push_back(potvar);
					potvar.clear();
				}
			}
			potvar += st[k];
		}
		if (k == st.length() - 1) {
			values2.push_back(potvar);
			potvar.clear();
		}

	}

	int top = values2.size();
	string ontop = values2[top - 1];

	it = mymap.find(ontop);
	if (it != mymap.end()) {
		value = mymap[ontop];
		temp = atof(value.c_str());


		return temp;

	}

	double val = atof(ontop.c_str());

	return val;

}
string inputmultiply(string equat) {
	string eq = equat;
	for (int i = 0; i < eq.length(); i++) {
		if (eq[i] == ')' && i < eq.length() - 1 && eq[i + 1] == '(') {
			string tempstr = eq.substr(0, i + 1) + '*' + eq.substr(i + 1);
			eq = tempstr;
			//i=0;
		}
	}

	for (int j = 1; j < eq.length(); j++) {
		if (eq[j] == '(' && isalnum(eq[j - 1]) )
		{
			string tempstr = eq.substr(0, j) + '*' + eq.substr(j);
			eq = tempstr;
			//j = 1;
		}
	}
	return eq;
}

string evaluate_all(string equation, map<string, string> vars)
{
	string lefthand;
	string righthand;
	int eval_start;
	int eval_end;
	string new_exp;
	bool eq_ended;
	int spots;
	double leftexp;
	double rightexp;
	double answer;
	string s_answer;
	int backsteps;
	int varStart;

	int here = 0;

	string eq = equation;


	for (int i = 0; i < eq.length(); i++) {
		if (eq[i] == '^') {
			backsteps = 0;
			eq_ended = 0;
			for (int h = i - 1; h >= 0; h--)
			{	

				if (eq[h] == '+' || eq[h] == '-' || eq[h] == '*' || eq[h] == '/' || eq[h] == '(' || eq[h] == ')') {
					eval_start = h + 1;
					lefthand = eq.substr(eval_start, backsteps);

					break;

				} 
				if (h == 0) {
					eval_start = 0;
					lefthand = eq.substr(eval_start, backsteps+1);

					break;

				}
				backsteps++;

			}
			for (int i = 0; i < lefthand.length(); i++) {
				if (isalpha(lefthand[i])) {
					varStart = i;
					break;
				}
			}
			spots = i - here;
			here = i + 1;
			/*looks for the occurance of the next operator and assigns everything
			from the ^ to that said operator to be the righthand operand*/
			for (int n = i; n < eq.length(); n++)
			{
				if (n == i+1 && eq[n] == '-') { continue; } //if the immediate next character is a minus, then it is meant to be a negative sign
				if (eq[n] == '+' || eq[n] == '-' || eq[n] == '*' || eq[n] == '/' || eq[n] == '(' || eq[n] == ')') {
					spots = n - here;
					eval_end = i + spots;

					i = n;
					break;

				}
				if (n == eq.length() - 1) {
					spots = n - here + 1;
					eval_end = i + spots;
					eq_ended = 1;

					i = n;
				}

			}

			righthand = eq.substr(here, spots);
			leftexp = getlastnum(lefthand,vars);
			rightexp = evalside(righthand,vars);


			answer = pow(leftexp, rightexp);
			s_answer = to_string(answer);

			if (eq_ended) 
			{
				if (i == 0) 
				{
				new_exp = eq.substr(0, eval_start + varStart) + s_answer;

				}
				else
				{
				new_exp = eq.substr(0, eval_start + varStart) + '*' + s_answer;

				}
			}
			else {
				if (i == 0) {
				new_exp = eq.substr(0, eval_start+varStart) + s_answer + eq.substr(eval_end + 1);

				}
				else
				{
				new_exp = eq.substr(0, eval_start+varStart) +'*'+ s_answer + eq.substr(eval_end + 1);

				}
			}

			eq = new_exp;

			i = 0;
		}
	}
	here = 0;

	for (int i = 0; i<eq.length(); i++)
	{
		if (eq[i] == '*' || eq[i] == '/') {
			char operation = eq[i];
			backsteps = 0;
			eq_ended = 0;

			for (int h = i - 1; h >= 0; h--)
			{
				if (eq[h] == '+' || eq[h] == '-' || eq[h] == '*' || eq[h] == '/' || eq[h] == '(' || eq[h] == ')') {
					eval_start = h + 1;
					lefthand = eq.substr(eval_start, backsteps);
					break;

				}
				if (h == 0) {
					eval_start = 0;
					lefthand = eq.substr(eval_start, backsteps + 1);
					break;

				}
				backsteps++;

			}

			spots = i - here;
			here = i + 1;
			for (int n = i + 1; n < eq.length(); n++)
			{
				if (eq[n] == '+' || eq[n] == '-' || eq[n] == '*' || eq[n] == '/' || eq[n] == '(' || eq[n] == ')') {
					spots = n - here;
					eval_end = i + spots;

					i = n;
					break;

				}
				if (n == eq.length() - 1) {
					spots = n - here + 1;
					eval_end = i + spots;
					eq_ended = 1;

					i = n;
				}

			}

			righthand = eq.substr(here, spots);

			leftexp = evalside(lefthand,vars);
			rightexp = evalside(righthand,vars);


			if (operation == '*') { answer = leftexp * rightexp; }
			if (operation == '/') { answer = leftexp / rightexp; }
			s_answer = to_string(answer);
			if (eq_ended) {
				new_exp = eq.substr(0, eval_start) + s_answer;
			}
			else {
				new_exp = eq.substr(0, eval_start) + s_answer + eq.substr(eval_end + 1);
			}

			eq = new_exp;
			i = 0;

		}
	}
	here = 0;
	for (int i = 0; i < eq.length(); i++) {
		if (eq[i] == '+' || eq[i] == '-') {
			char operation = eq[i];
			backsteps = 0;
			eq_ended = 0;
			if (i == 0) {
				eval_start = 0;
				operation = '*';
				lefthand = "-1";
			}
			for (int h = i - 1; h >= 0; h--)
			{

				if (eq[h] == '+' || eq[h] == '-' || eq[h] == '*' || eq[h] == '/' || eq[h] == '(' || eq[h] == ')') {
					eval_start = h + 1;
					lefthand = eq.substr(eval_start, backsteps);
					break;

				}
				if (h == 0) {
					eval_start = 0;

					lefthand = eq.substr(eval_start, backsteps+1);

					break;

				}
				backsteps++;

			}

			spots = i - here;
			here = i + 1;
			for (int n = i+1; n < eq.length(); n++)
			{
				if (eq[n] == '+' || eq[n] == '-' || eq[n] == '*' || eq[n] == '/' || eq[n] == '(' || eq[n] == ')') {
					spots = n - here;
					eval_end = i + spots;

					i = n;
					break;

				}
				if (n == eq.length() - 1) {
					spots = n - here + 1;
					eval_end = i + spots;
					eq_ended = 1;

					i = n;
				}

			}

			righthand = eq.substr(here, spots);
			leftexp = evalside(lefthand,vars);
			rightexp = evalside(righthand,vars);


			if (operation == '+') { answer = leftexp + rightexp; }
			if (operation == '-') { answer = leftexp - rightexp; }
			if (operation == '*') { answer = leftexp * rightexp; }

			s_answer = to_string(answer);
			if (eq_ended) {
				new_exp = eq.substr(0, eval_start) + s_answer;
			}
			else {
				new_exp = eq.substr(0, eval_start) + s_answer + eq.substr(eval_end + 1);
			}

			eq = new_exp;


			i = 0;
		}
	}
	return eq;
}



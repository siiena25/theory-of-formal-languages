## Lab1.1

Дана сигнатура для TRS. Написать алгоритм
унификации линейных термов в этой сигнатуре.

### Test:

Input constructors: g(1),A(0),f(2)  
Input variabes: x,y,z,w,v  
Input first term: f(w,g(A))  
Input second term: f(f(y,x),z)  

Parsed variable names: [x, y, z, w, v]  
Parsed constructor names: {g=g(1), f=f(2)}  
Parsed constants: [A]  

First term replacement: w=f(y, x)  
Second term replacement: z=g(A)  
Result of replacement: f(f(y, x), g(A)) 

## Lab1.2

Написать алгоритм проверки конфлюэнтности SRS по перекрытию.

### Test:

fgf -> ghhg  
hh ->  

System may be not confluent.
Have overlap in fgf: f.

## Lab2.1

Написать три эквивалентных (описывающих один и тот же язык) регулярных выражения:   
академическое; с использованием отрицания; с использованием ленивой итерации Клини.   
Минимальная длина regex — 10 символов.   
Сравнить производительность этих regex на 10 тестах длиной от 100 до 105 символов.  

Я взяла регулярное выражение для чисел в двоичной системе, делящихся на 3:  
academicRegex = "(0|1(01*0)*1)+".toRegex()  
negationRegex = "([^1]|[^0]([^1][^0]*[^1])*[^0])+".toRegex()  
lazyRegex = "(0|1(01*?0)*?1)+".toRegex()  

### Tests:

Расположены в lab2.1/tests.txt

### Output:

Time for academic/negation/lazy regex in ns:   
1 test: 	374087	136362	87287  
2 test: 	732400	793486	210850  
3 test: 	392184	371900	357417  	  
4 test: 	71287	108872	73576  	  
5 test: 	128818	203975	111101  
6 test: 	4995981	8152789	263143  	  
7 test: 	56534	42224	36867  	  
8 test: 	172603	164584	160845  
9 test: 	123255	131021	176328  	  
10 test: 	225934	327348	190156  	  

## Lab2.1

Реализовать алгоритм решения системы регулярных
выражений с беззвездными коэффициентами.

### Test:

X = (a|b)X+bY+aab  
Y=aX + a  

### Output:

(a|b)*(bY+aab)  
(a(a|b)*b)*(a+a(a|b)*aab+a(a|b)*bY)  

## Lab2.3

Реализовать алгоритм преобразования DFA в regex.

### Test:

S=q1  
A=q3,q4,q5  
E=a,b,c,d  
Q=q1,q2,q3,q4,q5  
q1,a=q2  
q2,b=q4  
q2,c=q3  
q2,d=q5  

### Output:

5-state DFA  
Start: q1  
Accept: q3 q4 q5   
All States: q1 q2 q3 q4 q5   
Transitions:   
q1 -a-> q2  
q2 -b-> q4  
q2 -c-> q3  
q2 -d-> q5  

Adding new start state (start)  
Adding new final state (finish)  

7-state GNFA  
Start: start  
Accept: finish   
All States: start q1 q2 q3 q4 q5 finish   
Transitions:   
q1 -a-> q2  
q2 -b-> q4  
q2 -c-> q3  
q2 -d-> q5  
q3 -ε-> finish  
q4 -ε-> finish  
q5 -ε-> finish  
start -ε-> q1  

Removing state q5  

6-state GNFA    
Start: start  
Accept: finish   
All States: start q1 q2 q3 q4 finish   
Transitions:   
q1 -a-> q2  
q2 -b-> q4  
q2 -c-> q3  
q2 -d-> finish  
q3 -ε-> finish  
q4 -ε-> finish  
start -ε-> q1  

Removing state q4  

5-state GNFA  
Start: start  
Accept: finish   
All States: start q1 q2 q3 finish   
Transitions:   
q1 -a-> q2  
q2 -c-> q3  
q2 -(b+d)-> finish  
q3 -ε-> finish  
start -ε-> q1  

Removing state q3  

4-state GNFA  
Start: start  
Accept: finish   
All States: start q1 q2 finish   
Transitions:   
q1 -a-> q2  
q2 -(c+(b+d))-> finish  
start -ε-> q1  

Removing state q2  

3-state GNFA  
Start: start  
Accept: finish   
All States: start q1 finish   
Transitions:   
q1 -a(c+(b+d))-> finish  
start -ε-> q1  

Removing state q1  

2-state GNFA  
Start: start  
Accept: finish   
All States: start finish   
Transitions:   
start -a(c+(b+d))-> finish  

Regex: a(c+(b+d))  



## Lab3

Реализовать алгоритм преобразования PDA в CFG.

### Test:

<q,A>$  
<q,a,A>-><q,B>  
<q,b,B>-><q,>  

### Output:

S -> A  
A -> aB  
B -> b  
![image](https://user-images.githubusercontent.com/85502799/142500501-366518e2-0f78-45cb-af8d-91e1da558f39.png)

### Test:

<q1,A>$  
<q1,a,A>-><q2,AA>  
<q2,b,B>-><q2,B>  
<q2,b,A>-><q2,>  

### Output:

S -> B  
B -> aFF  
F -> b  
![image](https://user-images.githubusercontent.com/85502799/142500625-a43bb65c-10b1-41c6-96d6-c450329860d1.png)

### Test:

<q1,A>$  
<q1,a,A>-><q2,AA>  
<q2,b,A>-><q3,A>  
<q3,c,A>-><q4,AAA>  
<q4,d,A>-><q4,>  

### Output:

S -> D  
D -> aHP  
H -> bL  
L -> cPPP  
P -> d  
![image](https://user-images.githubusercontent.com/85502799/142500724-67b5727a-6797-445e-b37f-7ae688e2ae26.png)

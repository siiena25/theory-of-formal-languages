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

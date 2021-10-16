## Lab1.1

Дана сигнатура для TRS. Написать алгоритм
унификации линейных термов в этой сигнатуре.

### Test1:

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

### Test2:

Input constructors: g(1),A(0),f(3)  
Input variabes: x,y,z,w,v  
Input first term: f(w,f(g(A),x,y),g(A))  
Input second term: f(f(y,x,g(z)),z,w)  

Parsed variable names: [x, y, z, w, v]  
Parsed constructor names: {g=g(1), f=f(3)}  
Parsed constants: [A]  

First term replacement: w=f(y, x, g(z))  
Second term replacement: z=f(g(A), x, y), w=g(A)  
Result of replacement: f(f(y, x, g(z)), f(g(A), x, y), g(A))  

### Test3:

Input constructors: g(1),A(0),q(3),f(2)  
Input variabes: x,y,z,w,v  
Input first term: f(q(x,y,z),f(z,z))  
Input second term: g(q(x,y,z))  

Parsed variable names: [x, y, z, w, v]  
Parsed constructor names: {g=g(1), q=q(3), f=f(2)}  
Parsed constants: [A]  

Unable to unity.

### Test4:

Input constructors: g(1),A(0),q(3),f(2)  
Input variabes: x  
Input first term: f(q(x,x,x),f(x,x))  
Input second term: f(x)  

Parsed variable names: [x]  
Parsed constructor names: {g=g(1), q=q(3), f=f(2)}  
Parsed constants: [A]  

Unable to parse term2: Number of arguments doesn't match: f

### Test5:

Input constructors: g(1),A(0),q(3),f(2)  
Input variabes: x  
Input first term: f(q(x,x,x),f(x,x))  
Input second term: f(x,x)  

Parsed variable names: [x]  
Parsed constructor names: {g=g(1), q=q(3), f=f(2)}  
Parsed constants: [A]  

First term replacement:  
Second term replacement: x=q(x, x, x), x=f(x, x)  
Result of replacement: f(q(x, x, x), f(x, x))  



## Lab1.2

Написать алгоритм проверки конфлюэнтности SRS по перекрытию.

### Test1:

fgf -> ghhg  
hh ->  

System may be not confluent.
Have overlap in fgf: f.

### Test2:

aabbclrdgoprlg;rlc -> x  
regrpdg -> y  
qweeretertr -> z  

System may be not confluent.
Have overlap in regrpdg: r.

### Test3:

qwerty -> ytrewq  
hh -> qwwwwq  

System may be not confluent.
Have overlap in hh: h.

### Test4:

abcdeed -> ee  
qwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww -> glgdpfglpdxmeortrjtvggf  
lplrpleptlcprlvp -> lgtvprptlpyht  
cfrsgtorceo -> troyvk6pylpy  

System is confluent.

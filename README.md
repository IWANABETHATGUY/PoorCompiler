# PoorCompiler
## 一个支持生成抽象语法树的4则运算解释器

小学期的大作业，完成一个支持四则运算的建议编译器，因为并没有生成任何目标语言，我直接在生成抽象语法树之后就进行求值了。

#### 以下为具体的bnf

> expr-> expr+term | expr-term | term  
> term->term * factor | term / factor | factor  
> factor-> digit specOpt | (expr)  
> specOpt-> /+ | /- | *+ | *- | +* | +/ | -* | -/ | ''

#### ```specOpt```是老师定义的特殊运算符，单目运算符。具体运算规则如下  
> y*- = y * y - y;  
> y*+ = y * y + y;  
> y/- = y / y - y;  
> y/+ = y / y + y;  
> y+* = (y + y) * y;  
> y-* = (y - y) * y;  
> y+/=(y + y) / y;  
> y-/= (y - y) / y;  

看个例子  
![](http://pezhnoefl.bkt.clouddn.com/result1.png)  

![](http://pezhnoefl.bkt.clouddn.com/result2.png)  
当我们输入上图中的字符串后将得到的ast如下  
![](http://pezhnoefl.bkt.clouddn.com/ast.png)  
这是在debugger中得到的数据结构，翻译成直观的树状图为：

![](http://pezhnoefl.bkt.clouddn.com/ast2.png)  




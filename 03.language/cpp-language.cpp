
//*========================================================================================================================
C++和lua的互相调用
Java和lua的互相调用机制

//============================================
如何理解常量指针与指针常量？
***const后面的内容为常量
const int p;      // p  为常量，初始化后不可更改
const int* p;     // *p 为常量，不能通过*p改变它指向的内容。指针常量.
int const* p;     // *p 为常量，同上
int* const p;     // p  为常量，初始化后不能再指向其它内容。常量指针.


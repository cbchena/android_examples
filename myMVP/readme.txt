使用MVP分层的Demo。

这里区别于一般的MVP：
1，Activity只负责展示与接收用户的输入；
2，Presenter负责处理View的逻辑和控制逻辑；
3，Model负责处理控制逻辑，包含bean；

Presenter去实现所有的接口，逻辑全是在其中去实现。
一般的MVP还是会将View的处理逻辑交给Activity，但这里将这一层的逻辑也抽离开，让Activity更简单、更直接。
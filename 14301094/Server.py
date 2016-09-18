#!/usr/bin/python
#coding:utf-8
import socket,thread
from time import sleep

def  MutiServer(clientsocket,addr):
    print "客户端连接地址: %s" % str(addr)
    msg = clientsocket.recv(2048).decode("UTF-8")
    print msg
    clientsocket.send(msg[::-1].encode("UTF-8"))
    sleep(1024)
    clientsocket.close()
    print "来自%s的链接已关闭"% addr
    
    
def main():
    serversocket = socket.socket() 
    host = socket.gethostname()
    port = 3333
    serversocket.bind((host, port))
    serversocket.listen(100)
    print "服务器开始运行,等待客户端连接"
    while True:
        clientsocket,addr = serversocket.accept()
        thread.start_new_thread(MutiServer,(clientsocket,addr))
        
if __name__ == '__main__':
    main()

    

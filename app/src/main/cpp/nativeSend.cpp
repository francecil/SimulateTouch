//
// Created by zhengjx on 2016/11/10.
//
#include <string>
#include <jni.h>
#include <android/log.h>
#include <string.h>
#include <errno.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <linux/input.h>
#include <sys/time.h>
#include <sys/types.h>
#include <unistd.h>
#define LOG_TAG "ZJX"
//#define LOGD(fmt, args...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, fmt, ##args)

// 单位毫秒
int tvs,tvus;
void LTSleep(struct timeval &tv,int nHm) {
    tv.tv_sec = tvs;
    tv.tv_usec = tvus + 1000 * nHm;
    if (tv.tv_usec > 1000000) {
        tv.tv_sec += tv.tv_usec / 1000000;
        tv.tv_usec %= 1000000;
    }
}
void simulate_down(int fd, int x, int y,int ms) {
    struct timeval tv;
    LTSleep(tv,ms);
    struct input_event pointEvent;
    pointEvent.type = EV_ABS;
    pointEvent.code = 0x2f;
    pointEvent.value = 0;
//    LTSleep(tv,ms+2);
    pointEvent.time = tv;
    write(fd, &pointEvent, sizeof(pointEvent)) ;

    struct input_event xEvent;
    xEvent.type = EV_ABS;
    xEvent.code = 0x35;
    xEvent.value = x;
//    LTSleep(tv,ms+4);
    xEvent.time = tv;
    write(fd, &xEvent, sizeof(xEvent)) ;

    struct input_event yEvent;
    yEvent.type = EV_ABS;
    yEvent.code = 0x36;
    yEvent.value = y;
//    LTSleep(tv,ms+6);
    yEvent.time = tv;
    write(fd, &yEvent, sizeof(yEvent)) ;

    struct input_event downEvent;
    downEvent.type = 1;
    downEvent.code = 330;
    downEvent.value = 1;
//    LTSleep(tv,ms+8);
    downEvent.time = tv;
    write(fd, &downEvent, sizeof(downEvent));

    struct input_event event;
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
//    LTSleep(tv,ms+10);
    event.time = tv;
    write(fd, &event, sizeof(event));

}

void simulate_move(int fd, int x, int y,int ms) {
    struct timeval tv;
    LTSleep(tv,ms);

    struct input_event xEvent;
    xEvent.type = EV_ABS;
    xEvent.code = 0x35;
    xEvent.value = x;
//    LTSleep(tv,ms+2);
    xEvent.time = tv;
    write(fd, &xEvent, sizeof(xEvent)) ;

    struct input_event yEvent;
    yEvent.type = EV_ABS;
    yEvent.code = 0x36;
    yEvent.value = y;
//    LTSleep(tv,ms+4);
    yEvent.time = tv;
    write(fd, &yEvent, sizeof(yEvent)) ;

    struct input_event event;
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
//    LTSleep(tv,ms+6);
    event.time = tv;
    write(fd, &event, sizeof(event));
}
void simulate_up(int fd,int ms) {
    struct timeval tv;
    LTSleep(tv,ms);

    struct input_event clearEvent;
    clearEvent.type = 3;
    clearEvent.code = 57;
    clearEvent.value = -1;
//    LTSleep(tv,ms+2);
    clearEvent.time = tv;
    write(fd, &clearEvent, sizeof(clearEvent));

    struct input_event upEvent;
    upEvent.type = 1;
    upEvent.code = 330;
    upEvent.value = 0;
//    LTSleep(tv,ms+4);
    upEvent.time = tv;
    write(fd, &upEvent, sizeof(upEvent));

    struct input_event event;
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
//    LTSleep(tv,ms+6);
    event.time = tv;
    write(fd, &event, sizeof(event));

}
// 模拟按键事件
void simulate_key(int fd,int kval) {
    struct input_event event;
    event.type = EV_KEY;
    event.value = 1;
    event.code = kval;

    gettimeofday(&event.time,0);
    write(fd,&event,sizeof(event)) ;

    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(fd, &event, sizeof(event));

    memset(&event, 0, sizeof(event));
    gettimeofday(&event.time, NULL);
    event.type = EV_KEY;
    event.code = kval;
    event.value = 0;
    write(fd, &event, sizeof(event));
    event.type = EV_SYN;
    event.code = SYN_REPORT;
    event.value = 0;
    write(fd, &event, sizeof(event));
}

extern "C"
jstring Java_com_example_zhengjx_myapplication_MainActivity_sendEvent(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    int fd_touch = open("/dev/input/event3", O_RDWR);
    if(fd_touch<=0) {
        return env->NewStringUTF(strerror(errno));
    }
    int x=400,y=400;
    //将系统当前时间以结构体形式返回给cur,初始时间戳
    struct timeval cur;
    gettimeofday(&cur, NULL);
    tvs=cur.tv_sec;
    tvus=cur.tv_usec;
    //DOMW事件 时间戳10
    int now=0;
    simulate_down(fd_touch, x, y,now);
    int i;
    int index=1;
    for(i=1;i<=5;i++){
        simulate_move(fd_touch, x+i*50, y+i*50,now+(++index));
    }
    for(i=1;i<=5;i++){
        simulate_move(fd_touch, x+(5-i)*250, y+i*50,now+(++index));
    }
    for(i=5;i>=1;i--){
        simulate_move(fd_touch, x-i*50, y+250,now+(++index));
    }
    for(i=5;i>=1;i--){
        simulate_move(fd_touch, x-i*50, y+i*50,now+(++index));
    }
//    simulate_move(fd_touch, 400, 400,100);
    simulate_up(fd_touch,now+(++index));

//    simulate_key(fd_touch, KEY_HOME);
    close(fd_touch);
    return env->NewStringUTF(strerror(errno));
}


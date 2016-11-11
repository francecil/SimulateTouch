package com.example.zhengjx.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by zhengjx on 2016/11/2.
 */

public class MTView extends SurfaceView implements SurfaceHolder.Callback {

    private static final int MAX_TOUCHPOINTS = 10;
    private static final String START_TEXT = "请随便触摸屏幕进行测试";
    private Paint textPaint = new Paint();
    private Paint touchPaints[] = new Paint[MAX_TOUCHPOINTS];
    private int colors[] = new int[MAX_TOUCHPOINTS];
    private float lastPosotion[][]=new float[MAX_TOUCHPOINTS][2];

    private int width, height;
    private float scale = 1.0f;
    private int index = 0;
    private ReceiveData rd;

    public MTView(Context context,ReceiveData rd) {
        super(context);
        this.rd=rd;
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true); // 确保我们的View能获得输入焦点
        setFocusableInTouchMode(true); // 确保能接收到触屏事件
        init();
    }
    public MTView(Context context) {
        super(context);
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);
        setFocusable(true); // 确保我们的View能获得输入焦点
        setFocusableInTouchMode(true); // 确保能接收到触屏事件
        init();
    }
    private void init() {
        // 初始化10个不同颜色的画笔
        textPaint.setColor(Color.WHITE);
        colors[0] = Color.BLUE;
        colors[1] = Color.RED;
        colors[2] = Color.GREEN;
        colors[3] = Color.YELLOW;
        colors[4] = Color.CYAN;
        colors[5] = Color.MAGENTA;
        colors[6] = Color.DKGRAY;
        colors[7] = Color.WHITE;
        colors[8] = Color.LTGRAY;
        colors[9] = Color.GRAY;
        for (int i = 0; i < MAX_TOUCHPOINTS; i++) {
            touchPaints[i] = new Paint();
            touchPaints[i].setColor(colors[i]);
        }
    }


    /**
     * 处理触屏事件
     * 目标：新手指DOWN时 发生命令：
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //ACTION_POINTER_i_DOWN:5 261 517 773 存在触点的情况下 DOWN
        //ACTION_POINTER_i_UP:6 262 518 774   存在触点的情况下 UP
        //每次UP时返回index后，再次UP时系统会重做填充，
        //即按顺序1 2 3 4 DOWN后， UP了1 返回6 此时UP 2 返回的还是6 说明再次UP时会把触点INDEX往前填充
        //多个触点时，UP第一个返回的是6，但是只有一个触点时返回的是2
        //按顺序1 2 3 4 DOWN后，UP 1返回6，此时再DOWN 5返回的是5，说明再次DOWN会从头找开始填充
        //UP时，通过event.getPointerCount()得到的数量不会变，只有再次才会更新，因为我们需要得到UP点的x,y等信息
        // DOWN 0 1 2 后 UP 了 1 此时剩 0 2 getPointerId(1)可以返回i=1的点id为2
        //需要理清的是pointerIndex与pointerID非等同
        //非MOVE事件，触点坐标也可能改变。例：1 2指按，1指抬，ACTION=6 此时获取2指的x,y可能会与1指未抬时的位置不一致
        //因为触摸屏是按一定时间间隔的，在这很小的时间间隔内，同时出现了MOVE和UP,优先UP,所以ACTION=UP 但是位置还是会移动

        //压力 等传感器 暂时不考虑，触摸点直径也暂时不考虑

        //出现哪些事件需要发数据，发什么数据？与sendEvent对应
        //0.(重)进游戏界面时，发送清空所有触点的指令
        //1.DOWN  or UP 发其位置 SENDEVENT 1 or 0 具体数据 同时更新 删除数组数据
        //2.MOVE  按ID得到新的位置 与之前进行比较

        String rec="";
        int pointerCount = event.getPointerCount();
        if (pointerCount > MAX_TOUCHPOINTS) {
            pointerCount = MAX_TOUCHPOINTS;
        }
        int opt=event.getAction();
        boolean isFirst=true;
        if(opt==2){
            for (int i = 0; i < pointerCount; i++) {
                int id = event.getPointerId(i);
                int x = (int) event.getX(i);
                int y = (int) event.getY(i);
                if(isFirst){isFirst=false;}
                else rec+=";";
                rec+=id+",2,"+x+","+y;
            }
        }else if(opt==0||(opt-5)%256==0){
            //为了简化，我们假设这过程不会进行MOVE
            int ind=(event.getAction()-5)/256;
            for (int i = 0; i < pointerCount; i++) {
                if(i==ind){
                    int id = event.getPointerId(i);
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);
                    if(isFirst){isFirst=false;}
                    else rec+=";";
                    rec+=id+",0,"+x+","+y;
                    break;
                }
            }
        }else if(opt==1||(opt-6)%256==0){
            int ind=(event.getAction()-6)/256;
            for (int i = 0; i < pointerCount; i++) {
                if(i==ind){
                    int id = event.getPointerId(i);
                    if(isFirst){isFirst=false;}
                    else rec+=";";
                    rec+=id+",1";
                    break;
                }
            }
        }
        if(rd!=null)
        rd.receive(rec);
        //绘制
        Canvas c = getHolder().lockCanvas();

        if (c != null) {
            //某个手指UP
            /*
            if ((event.getAction()-6)%256==0) {
                int ind=(event.getAction()-6)/256;
                for (int i = 0; i < pointerCount; i++) {
                    if(i==ind){
                        continue;
                    }
                    int id = event.getPointerId(i);
                    if(lastPosotion[id][0]<=0&&lastPosotion[id][1]<=0){
                        lastPosotion[id][0]=event.getX(i);
                        lastPosotion[id][1]=event.getY(i);
                    }else{
//                        drawLine(event.getX(i),event.getY(i),lastPosotion[id][0],lastPosotion[id][1],touchPaints[id],c);
                        Log.i("x,y","lx:"+lastPosotion[id][0]+",ly:"+lastPosotion[id][1]+";x:"+event.getX(i)+",y:"+event.getY(i));
                        lastPosotion[id][0]=event.getX(i);
                        lastPosotion[id][1]=event.getY(i);

                    }
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);

                    drawCrosshairsAndText(x, y, touchPaints[id], index++, id, c);
                }
                for (int i = 0; i < pointerCount; i++) {
                    if(i==ind){
//                        Log.i("i=",""+i);
                        continue;
                    }
                    int id = event.getPointerId(i);
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);
                    drawCircle(x, y, touchPaints[id], c);
                }
            } else
            */
            if(event.getAction() == MotionEvent.ACTION_UP){
//                Log.i("zjx","全部手指离开:"+event.getActionIndex());
            }
            else {
                // 重绘：非UP事件 则获取每个触控点的位置并绘制
                // 在每一个触点上绘制一个十字和坐标信息
                for (int i = 0; i < pointerCount; i++) {
                    int id = event.getPointerId(i);
//                    if(lastPosotion[id][0]<=0&&lastPosotion[id][1]<=0){
//                        lastPosotion[id][0]=event.getX(i);
//                        lastPosotion[id][1]=event.getY(i);
//                    }else{
////                        drawLine(event.getX(i),event.getY(i),lastPosotion[id][0],lastPosotion[id][1],touchPaints[id],c);
//                        Log.i("x,y","lx:"+lastPosotion[id][0]+",ly:"+lastPosotion[id][1]+";x:"+event.getX(i)+",y:"+event.getY(i));
//                        lastPosotion[id][0]=event.getX(i);
//                        lastPosotion[id][1]=event.getY(i);
//
//                    }
                    Log.i("x,y","x:"+event.getX(i)+",y:"+event.getY(i));
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);
//                    drawCrosshairsAndText(x,y, touchPaints[id], index++, id, c);
                }

                // 在每一个触点上绘制一个圆
                for (int i = 0; i < pointerCount; i++) {
                    // DOWN 0 1 2 后 UP 了 1 此时剩 0 2 getPointerId(1)可以返回i=1的点id为2
                    int id = event.getPointerId(i);
                    int x = (int) event.getX(i);
                    int y = (int) event.getY(i);
                    drawCircle(x, y, touchPaints[id], c);
                }

            }
            Log.i("x,y","======");
            // 画完后，unlock
            getHolder().unlockCanvasAndPost(c);
        }


        return true;
    }

    /**
     * 画十字及坐标信息
     *
     * @param x
     * @param y
     * @param paint
     * @param ptr
     * @param id
     * @param c
     */
    private void drawCrosshairsAndText(float x, float y, Paint paint, int ptr,
                                       int id, Canvas c) {
//        c.drawLine(0, y, width, y, paint);
//        c.drawLine(x, 0, x, height, paint);
        int textY = (int) ((15 + 20 * ptr) * scale);
        c.drawText("x" + ptr + "=" + x, 10 * scale, textY, textPaint);
        c.drawText("y" + ptr + "=" + y, 70 * scale, textY, textPaint);
        c.drawText("id" + ptr + "=" + id, width - 55 * scale, textY, textPaint);
    }
    private void drawLine(float x, float y,float lx,float ly, Paint paint, Canvas c) {
        c.drawLine(x, y, lx, ly, paint);
    }
    /**
     * 画圆
     *
     * @param x
     * @param y
     * @param paint
     * @param c
     */
    private void drawCircle(int x, int y, Paint paint, Canvas c) {
        c.drawCircle(x, y, 5 * scale, paint);
    }

    /**
     * 进入程序时背景画成黑色，然后把START_TEXT写到屏幕
     */
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        if (width > height) {
            this.scale = width / 480f;
        } else {
            this.scale = height / 480f;
        }
        textPaint.setTextSize(14 * scale);
        Canvas c = getHolder().lockCanvas();
        if (c != null) {
            c.drawColor(Color.BLACK);
            float tWidth = textPaint.measureText(START_TEXT);
            c.drawText(START_TEXT, width / 2 - tWidth / 2, height / 2, textPaint);
            getHolder().unlockCanvasAndPost(c);
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("zjx", "surfaceCreated");
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i("zjx", "surfaceDestroyed");
    }
}
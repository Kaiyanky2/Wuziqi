package com.wangkaiyan.app.wuziqi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangkaiyan on 16/6/11.
 */
public class WuziqiPanel extends View {

    private int mPanelWidth;
    private float mLineHeight;

    private static final int MAX_LINE = 10;

    private int MAX_COUNT_IN_LINE = 5;

    private Paint mPaint = new Paint();

    private Bitmap mWhitePiece;
    private Bitmap mBlackPiece;

    private float ratePieceOfLineHeight = 3*1.0f / 4;

    /**
     * 存储棋子坐标的集合
     */
    private List<Point> mWhiteArray = new ArrayList<>();
    private List<Point> mBlackArray = new ArrayList<>();

    /**
     * 白棋先下或者轮到白棋
     */
    private boolean mIsWhite = true;

    /**
     * 游戏结束
     */
    private boolean mIsGameOver = false;

    /**
     * 白子胜
     */
    private boolean mWhiteIsWinner = false;

    public WuziqiPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(0x44ff0000);

        init();
    }

    private void init(){
        mPaint.setColor(0x88000000);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true); // 抗锯齿
        mPaint.setStyle(Paint.Style.STROKE);

        mWhitePiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_w2);
        mBlackPiece = BitmapFactory.decodeResource(getResources(), R.drawable.stone_b1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int width = Math.min(widthSize, heightSize);

        if (widthMode == MeasureSpec.UNSPECIFIED){
            width = heightSize;
        }else if (heightMode == MeasureSpec.UNSPECIFIED){
            width = widthSize;
        }

        setMeasuredDimension(width, width);
    }

    /**
     * 有关尺寸的操作
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        mPanelWidth = w;
        mLineHeight = mPanelWidth * 1.0f / MAX_LINE;

        int pieceWidth = (int) (mLineHeight * ratePieceOfLineHeight);
        mWhitePiece = Bitmap.createScaledBitmap(mWhitePiece, pieceWidth, pieceWidth, false);
        mBlackPiece = Bitmap.createScaledBitmap(mBlackPiece, pieceWidth, pieceWidth, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制棋盘
        drawBoard(canvas);

        //绘制棋子
        drawPiece(canvas);
        
        //检查游戏是否结束
        checkGameOver();
    }

    private void drawBoard(Canvas canvas){
        int w = mPanelWidth;
        float lineHeight = mLineHeight; //行高这样的数据尽可能使用float类型,否则会有精度的丢失

        for (int i = 0; i < MAX_LINE; i++) {
            // 绘制横线
            int startX = (int) (lineHeight / 2);
            int endX = (int) (w - lineHeight / 2);
            int y = (int) ((0.5 + i) * lineHeight);
            canvas.drawLine(startX, y, endX, y, mPaint);

            // 绘制纵线
            canvas.drawLine(y, startX, y, endX, mPaint);
        }
    }

    private void drawPiece(Canvas canvas) {
//        //绘制白子
//        for (int i = 0, n = mWhiteArray.size(); i < n; i++) {
//            Point whitePoint = mWhiteArray.get(i);
//            canvas.drawBitmap(mWhitePiece,
//                    whitePoint.x + ((1 - ratePieceOfLineHeight) / 2 )*mLineHeight,
//                    whitePoint.y + ((1 - ratePieceOfLineHeight) / 2 )*mLineHeight,
//                    null);
//        }
//
//        //绘制黑子
//        for (int i = 0, n = mBlackArray.size(); i < n; i++) {
//            Point blackPoint = mBlackArray.get(i);
//            canvas.drawBitmap(mBlackPiece,
//                    blackPoint.x + ((1 - ratePieceOfLineHeight) / 2 )*mLineHeight,
//                    blackPoint.y + ((1 - ratePieceOfLineHeight) / 2 )*mLineHeight,
//                    null);
//        }

        for(int i =0,n = mWhiteArray.size();i < n; i++)
        {
            Point whitePoint = mWhiteArray.get(i);
            canvas.drawBitmap(mWhitePiece,
                    (whitePoint.x + (1-ratePieceOfLineHeight) / 2) * mLineHeight,
                    (whitePoint.y + (1- ratePieceOfLineHeight) / 2) * mLineHeight,null);
        }
        for(int i =0,n = mBlackArray.size();i < n; i++)
        {
            Point blackPoint = mBlackArray.get(i);
            canvas.drawBitmap(mBlackPiece,
                    (blackPoint.x + (1- ratePieceOfLineHeight) / 2) * mLineHeight,
                    (blackPoint.y + (1- ratePieceOfLineHeight) / 2) * mLineHeight,null);
        }
    }

    private void checkGameOver() {
        boolean whiteWin = checkFiveInLine(mWhiteArray);
        boolean blackWin = checkFiveInLine(mBlackArray);

        if (whiteWin || blackWin){
            mIsGameOver = true;
            mWhiteIsWinner = whiteWin;

            String text = mWhiteIsWinner? "白棋赢!": "黑棋赢";
            Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 检查是否五子连线
     * @param points
     * @return
     */
    private boolean checkFiveInLine(List<Point> points) {
        for (Point p :
                points) {
            int x = p.x;
            int y = p.y;

            boolean win = checkHorizontal(x, y, points);
            if (win){
                return true;
            }
            win = checkVertical(x, y, points);
            if (win){
                return true;
            }
            win = checkLeftDiagonal(x, y, points);
            if (win){
                return true;
            }
            win = checkRightDiagonal(x, y, points);
            if (win){
                return true;
            }

        }
        return false;
    }

    /**
     * 判断x,y位置的棋子是否横向有相邻的五个一致
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkHorizontal(int x, int y, List<Point> points) {
        int count = 1;
        //左边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x - i, y))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        //右边
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x + i, y))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        return false;
    }

    /**
     * 检查x,y位置的棋子是否有纵向相邻的五个棋子
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkVertical(int x, int y, List<Point> points) {
        int count = 1;
        //上
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x, y - i))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        //下
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x, y + i))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        return false;
    }

    /**
     * 检查x,y位置的棋子是否有左斜向相邻的五个棋子
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkLeftDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //上斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x - i, y + i))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        //下斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x + i, y - i))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        return false;
    }

    /**
     * 检查x,y位置的棋子是否有右斜向相邻的五个棋子
     * @param x
     * @param y
     * @param points
     * @return
     */
    private boolean checkRightDiagonal(int x, int y, List<Point> points) {
        int count = 1;
        //上斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x - i, y - i))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        //下斜
        for (int i = 1; i < MAX_COUNT_IN_LINE; i++){
            if (points.contains(new Point(x + i, y + i))){
                count++;
            }else{
                break;
            }
        }

        if (count == MAX_COUNT_IN_LINE){
            return true;
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //游戏结束
        if (mIsGameOver){
            return false;
        }

        int action = event.getAction();
        if (action == MotionEvent.ACTION_UP){
            int x = (int) event.getX();
            int y = (int) event.getY();
//            Point point = new Point(x, y);
            Point point = getValidPoint(x, y);

            //判断此点是否已经被占用
            if (mWhiteArray.contains(point) || mBlackArray.contains(point)){
                return false;
            }

            if (mIsWhite){
                mWhiteArray.add(point);
            }else{
                mBlackArray.add(point);
            }

            invalidate(); //请求重绘
            mIsWhite = !mIsWhite;

            return  true;

        }
        return true;    //上面改成ACTION_UP，则此处要改为true，否则不出现棋子。
    }

    private Point getValidPoint(int x, int y) {
        return new Point((int) (x / mLineHeight), (int)(y / mLineHeight));
    }
}

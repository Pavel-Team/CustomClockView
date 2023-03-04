package ru.pt.testvk

import android.content.Context
import android.graphics.*
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.View
import java.util.*
import kotlin.math.*


private const val TAG = "CustomClockView"
private const val TIME_UPDATE_CLOCK = 1000L //Минимальное время в мс, через которое происходит переотрисовка часов
private const val WIDTH_WRAP_CONTENT = 100  //Ширина по умолчанию при режиме wrap_content
private const val HEIGHT_WRAP_CONTENT = 100 //Высота по умолчанию при режиме wrap_content

/**
 * View для отображения часов, указывающих время на устройстве пользователя
 * CustomClockView автоматически растягивается в зависимости от размеров View
 * (чтобы сохранить квадратное соотношение включите программно параметр isCircle = true)
 *
 * Имеется возможность программно изменять параметры отображения View, такие как:
 * ratioWidthClockStroke: Float = 0.02f - Ширина обводки часов (по умолчанию 2% от минимальных размеров View)
 * ratioPaddingDot: Float = 0.1f        - Отступ отметки для минутной/секундной стрелки от края часов (по умолчанию 10% от радиуса часов)
 * ratioRadiusSmallDot: Float = 0.01f   - Радиус маленькой отметки для минутной/секундной стрелки (по умолчанию 1% от минимальных размеров View)
 * ratioRadiusBigDot: Float = 0.017f    - Радиус маленькой отметки для минутной/секундной стрелки (по умолчанию 1,7% от минимальных размеров View)
 * ratioTextSize = 0.1f                 - Размер шрифта (по умолчанию 10% от минимальных размеров View)
 * ratioPaddingNumber: Float = 0.26f    - Отступ отметки для цифры от края часов (по умолчанию 26% от размеров View)
 * ratioWidthSecondHand: Float = 0.01f  - Ширина секундной стрелки (по умолчанию 1% от минимальных размеров View)
 * ratioWidthMinuteHand: Float = 0.02f  - Ширина минутной стрелки (по умолчанию 2% от минимальных размеров View)
 * ratioWidthHourHand: Float = 0.03f    - Ширина часовой стрелки (по умолчанию 3% от минимальных размеров View)
 * ratioLengthTailHand: Float = 0.2f    - Длина хвостика часовых стрелок (по умолчанию 20% от радиуса часов)
 * ratioLengthSecondHand: Float = 0.85f - Отношение длины секундной стрелки к радиусу часов (по умолчанию 85% от радиуса часов)
 * ratioLengthMinuteHand: Float = 0.8f  - Отношение длины секундной стрелки к радиусу часов (по умолчанию 80% от радиуса часов)
 * ratioLengthHourHand: Float = 0.7f    - Отношение длины секундной стрелки к радиусу часов (по умолчанию 70% от радиуса часов)
 *
 * colorBorderClock: Int = Color.BLACK     - Цвет обводки (рамки) часов
 * colorBackgroundClock: Int = Color.BLACK - Цвет фона на часах
 * colorDot: Int = Color.BLACK             - Цвет отметок (точек) для минутных/секндных стрелок
 * colorNumber: Int = Color.BLACK          - Цвет для цифр
 * colorSecondHand: Int = Color.BLACK      - Цвет для секундной стрелки
 * colorMinuteHand: Int = Color.BLACK      - Цвет для минутной стрелки
 * colorHourHand: Int = Color.BLACK        - Цвет для часовой стрелки
 * */
class CustomClockView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    //Параметры для отрисовки часов (могут быть изменены программно)
    var isCircle = false                     //Изменить пропорции так, чтобы осталось часы приняли форму кргуа

    var ratioWidthClockStroke: Float = 0.02f //Ширина обводки часов (по умолчанию 2% от минимальных размеров View)
    var ratioPaddingDot: Float = 0.1f        //Отступ отметки для минутной/секундной стрелки от края часов (по умолчанию 10% от радиуса часов)
    var ratioRadiusSmallDot: Float = 0.01f   //Радиус маленькой отметки для минутной/секундной стрелки (по умолчанию 1% от минимальных размеров View)
    var ratioRadiusBigDot: Float = 0.017f    //Радиус маленькой отметки для минутной/секундной стрелки (по умолчанию 1,7% от минимальных размеров View)
    var ratioTextSize = 0.1f                 //Размер шрифта (по умолчанию 10% от минимальных размеров View)
    var ratioPaddingNumber: Float = 0.26f    //Отступ отметки для цифры от края часов (по умолчанию 26% от размеров View)
    var ratioWidthSecondHand: Float = 0.01f  //Ширина секундной стрелки (по умолчанию 1% от минимальных размеров View)
    var ratioWidthMinuteHand: Float = 0.02f  //Ширина минутной стрелки (по умолчанию 2% от минимальных размеров View)
    var ratioWidthHourHand: Float = 0.03f    //Ширина часовой стрелки (по умолчанию 3% от минимальных размеров View)
    var ratioLengthTailHand: Float = 0.2f    //Длина хвостика часовых стрелок (по умолчанию 20% от радиуса часов)
    var ratioLengthSecondHand: Float = 0.85f //Отношение длины секундной стрелки к радиусу часов (по умолчанию 85% от радиуса часов)
    var ratioLengthMinuteHand: Float = 0.8f  //Отношение длины секундной стрелки к радиусу часов (по умолчанию 80% от радиуса часов)
    var ratioLengthHourHand: Float = 0.7f    //Отношение длины секундной стрелки к радиусу часов (по умолчанию 70% от радиуса часов)

    var colorBorderClock: Int = Color.BLACK     //Цвет обводки (рамки) часов
    var colorBackgroundClock: Int = Color.WHITE //Цвет фона на часах
    var colorDot: Int = Color.BLACK             //Цвет отметок (точек) для минутных/секндных стрелок
    var colorNumber: Int = Color.BLACK          //Цвет для цифр
    var colorSecondHand: Int = Color.BLACK      //Цвет для секундной стрелки
    var colorMinuteHand: Int = Color.BLACK      //Цвет для минутной стрелки
    var colorHourHand: Int = Color.BLACK        //Цвет для часовой стрелки



    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textAlignment = TEXT_ALIGNMENT_CENTER
        textSize = 20.0f
        typeface = Typeface.create("", Typeface.BOLD)
    }
    private lateinit var viewHandler: Handler


    init {
        viewHandler = Handler(Looper.getMainLooper())
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //Определяем установленные режимы и размеры View
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        //Итоговые размеры View
        var width = 0
        var height = 0

        //Определяем ширину
        width = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                min(WIDTH_WRAP_CONTENT, widthSize)
            }
            else -> {
                WIDTH_WRAP_CONTENT
            }
        }

        //Определяем высоту
        height = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                min(HEIGHT_WRAP_CONTENT, heightSize)
            }
            else -> {
                HEIGHT_WRAP_CONTENT
            }
        }

        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        if (canvas != null) {
            //Отрисовка текущего кадра часов (в зависимости от режима isCircle вызываем разные функции)
            if (isCircle) {
                drawBackgroundCircleClock(canvas)
                drawHandsCircleClock(canvas)
            } else {
                drawBackgroundClock(canvas)
                drawHandsClock(canvas)
            }

            //Планируем повторную отрисовку через TIME_UPDATE_CLOCK мс в этом же потоке
            viewHandler.postDelayed(
                Runnable {
                         invalidate()
                },
                TIME_UPDATE_CLOCK
            )
        }
    }


    /** Отрисовка фона растягиваемых часов (isCircle = false) (часы без стрелок)
     * На вход принимает 1 параметр:
     *  canvas: Canvas? - холст, на котором необходимо выполнить отрисовку
     * Внутри происходит рисование часов по следующим правилам:
     *  Ширина обводки часов - ratioWidthClockStroke (по умолчанию: 2% от размеров View)
     *  Радиус отметок минутной и секундной стрелки - ratioRadiusSmallDot (по умолчанию 1% от минимальных размеров View)
     *      (ratioRadiusBigDot (по умолчанию) 1.7% - для каждой пятой отметки). Отступ от края рамки - ratioPaddingDot (по умолчанию 10%)
     *  Размер шрифта цифр - ratioTextSize (по умолчанию 10% от минимальных размеров View) */
    private fun drawBackgroundClock(canvas: Canvas) {
        val minSizeCanvas = min(canvas.width, canvas.height)           //Минимальные размеры View
        val radiusWidth = canvas.width / 2f                            //Длина главная полуоси OX эллипсиса
        val radiusHeight = canvas.height / 2f                          //Длина главная полуоси OY эллипсиса
        val deltaWidthBorder = minSizeCanvas * ratioWidthClockStroke   //Ширина обводки часов
        val textSize = minSizeCanvas * ratioTextSize                   //Размер шрифта для цифр
        paint.textSize = textSize

        //Отрисовка фона и обводки часов
        paint.color = colorBorderClock
        canvas.drawOval(0f, 0f, canvas.width.toFloat(), canvas.height.toFloat(), paint)
        paint.color = colorBackgroundClock
        canvas.drawOval(deltaWidthBorder, deltaWidthBorder, canvas.width-deltaWidthBorder, canvas.height-deltaWidthBorder, paint)

        //Отрисовка отметок минутной и секундной стрелки
        paint.color = colorDot
        for (i in 0..59) {
            //Угол до отметки
            val angle = PI.toFloat() * (i / 30f - 0.5f)
            //Радиус овала при заданном угле angle
            val radiusOval = (radiusWidth*radiusHeight) / (sqrt(sin(angle)*sin(angle)*radiusWidth*radiusWidth + cos(angle)*cos(angle)*radiusHeight*radiusHeight))
            //Координаты отметки минутной/секундной стрелки с учетом отступа в 10%
            val xDot = radiusWidth + cos(angle)*radiusOval * (1f - ratioPaddingDot)
            val yDot = radiusHeight + sin(angle)*radiusOval * (1f - ratioPaddingDot)
            //Радиус отметки (для каждой пятой отметки - меняем радиус отметки и рисуем цифру)
            var radiusDot = minSizeCanvas * ratioRadiusSmallDot
            if (i%5==0) {
                paint.color = colorNumber
                radiusDot = minSizeCanvas * ratioRadiusBigDot
                val xNumber = radiusWidth + cos(angle)*radiusOval * (1f - ratioPaddingNumber)
                val yNumber = radiusHeight + sin(angle)*radiusOval * (1f - ratioPaddingNumber) - ((paint.descent() + paint.ascent()) / 2f)
                canvas.drawText(((i/5+11)%12+1).toString(), xNumber, yNumber, paint)
                paint.color = colorDot
            }

            canvas.drawCircle(xDot, yDot, radiusDot, paint)
        }
    }


    /**Отрисовка стрелок растягиваемых часов (isCircle = false)
     * На вход принимает 1 параметр:
     *  canvas: Canvas? - холст, на котором необходимо выполнить отрисовку
     * Внутри происходит рисование стрелок по следующим правилам:
     *  Радиус секундной стрелки - ratioLengthSecondHand (85% по умолчанию) от радиуса часов + ratioLengthTailHand (20% по умолчанию) хвостик.
     *      Ширина стрелки - ratioWidthSecondHand (по умолчанию 1% от минимальных размеров View)
     *  Радиус минутной стрелки - ratioLengthMinuteHand (80% по умолчанию) от радиуса часов + ratioLengthTailHand (20% по умолчанию) хвостик.
     *      Ширина стрелки - ratioWidthMinuteHand (по умолчанию 2% от минимальных размеров View)
     *  Радиус часовой стрелки - ratioLengthHourHand (70% по умолчанию) от радиуса часов + ratioLengthTailHand (20% по умолчанию) хвостик.
     *      Ширина стрелки - ratioWidthHourHand (по умолчанию 3% от минимальных размеров View)*/
    private fun drawHandsClock(canvas: Canvas) {
        //Получаем текущее время
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        //Рассчеты для отрисовки стрелок
        val minSizeCanvas = min(canvas.width, canvas.height) //Минимальные размеры View
        val radiusWidth = canvas.width / 2f                  //Длина главная полуоси OX эллипсиса
        val radiusHeight = canvas.height / 2f                //Длина главная полуоси OY эллипсиса

        //Отрисовка секундной стрелки
        paint.strokeWidth = minSizeCanvas * ratioWidthSecondHand
        paint.color = colorSecondHand
        val angleSecond = PI.toFloat() * (seconds / 30f - 0.5f) //Угол секундной стрелки
        val radiusSecondHand = (radiusWidth*radiusHeight) /
                (sqrt(sin(angleSecond)*sin(angleSecond)*radiusWidth*radiusWidth + cos(angleSecond)*cos(angleSecond)*radiusHeight*radiusHeight)) //Радиус овала при зданном angleSecond
        val xStartSecondHand = radiusWidth - cos(angleSecond)*radiusSecondHand*ratioLengthTailHand
        val yStartSecondHand = radiusHeight - sin(angleSecond)*radiusSecondHand*ratioLengthTailHand
        val xStopSecondHand = radiusWidth + cos(angleSecond) * radiusSecondHand * ratioLengthSecondHand
        val yStopSecondHand = radiusHeight + sin(angleSecond) * radiusSecondHand * ratioLengthSecondHand
        canvas.drawLine(xStartSecondHand, yStartSecondHand, xStopSecondHand, yStopSecondHand, paint)

        //Отрисовка минутной стрелки
        paint.strokeWidth = minSizeCanvas * ratioWidthMinuteHand
        paint.color = colorMinuteHand
        val angleMinute = PI.toFloat() * (minutes / 30f - 0.5f) + (PI.toFloat() * seconds / 60f / 30f) //Угол минутной стрелки с учетом секундной стрелки
        val radiusMinuteHand = (radiusWidth*radiusHeight) /
                (sqrt(sin(angleMinute)*sin(angleMinute)*radiusWidth*radiusWidth + cos(angleMinute)*cos(angleMinute)*radiusHeight*radiusHeight)) //Радиус овала при заданном angleMinute
        val xStartMinuteHand = radiusWidth - cos(angleMinute)*radiusMinuteHand*ratioLengthTailHand
        val yStartMinuteHand = radiusHeight - sin(angleMinute)*radiusMinuteHand*ratioLengthTailHand
        val xStopMinuteHand = radiusWidth + cos(angleMinute) * radiusMinuteHand * ratioLengthMinuteHand
        val yStopMinuteHand = radiusHeight + sin(angleMinute) * radiusMinuteHand * ratioLengthMinuteHand
        canvas.drawLine(xStartMinuteHand, yStartMinuteHand, xStopMinuteHand, yStopMinuteHand, paint)

        //Отрисовка часовой стрелки
        paint.strokeWidth = minSizeCanvas * ratioWidthHourHand
        paint.color = colorHourHand
        val angleHour = PI.toFloat() / 2f * (hour % 12 / 3 - 1) + (PI.toFloat() * minutes / 60f / 6f) //Угол часовой стрелки с учетом минутной стрелки
        val radiusHourHand = (radiusWidth*radiusHeight) /
                (sqrt(sin(angleHour)*sin(angleHour)*radiusWidth*radiusWidth + cos(angleHour)*cos(angleHour)*radiusHeight*radiusHeight)) //Радиус овала при заданном angleHour
        val xStartHourHand = radiusWidth - cos(angleHour)*radiusHourHand*ratioLengthTailHand
        val yStartHourHand = radiusHeight - sin(angleHour)*radiusHourHand*ratioLengthTailHand
        val xStopHourHand = radiusWidth + cos(angleHour) * radiusHourHand * ratioLengthHourHand
        val yStopHourHand = radiusHeight + sin(angleHour) * radiusHourHand * ratioLengthHourHand
        canvas.drawLine(xStartHourHand, yStartHourHand, xStopHourHand, yStopHourHand, paint)
    }


    /** Отрисовка фона круглых часов (isCircle = true) (часы без стрелок)
     * На вход принимает 1 параметр:
     *  canvas: Canvas? - холст, на котором необходимо выполнить отрисовку
     * Внутри происходит рисование часов по следующим правилам:
     *  Ширина обводки часов - ratioWidthClockStroke (по умолчанию: 2% от размеров View)
     *  Радиус отметок минутной и секундной стрелки - ratioRadiusSmallDot (по умолчанию 1% от минимальных размеров View)
     *      (ratioRadiusBigDot (по умолчанию) 1.7% - для каждой пятой отметки). Отступ от края рамки - ratioPaddingDot (по умолчанию 10%)
     *  Размер шрифта цифр - ratioTextSize (по умолчанию 10% от минимальных размеров View) */
    private fun drawBackgroundCircleClock(canvas: Canvas) {
        val minSizeCanvas = min(canvas.width, canvas.height)                                  //Минимальные размеры View
        val radius = minSizeCanvas / 2f                                                       //Радиус часов
        val deltaRadiusWidth = if (canvas.width>canvas.height) (canvas.width - canvas.height) / 2f else 0f  //Разница между полуосями овала по ширине (если отрицательна, то = 0)
        val deltaRadiusHeight = if (canvas.height>canvas.width) (canvas.height - canvas.width) / 2f else 0f //Разница между полуосями овала по высоте (если отрицательна, то = 0)
        val deltaWidthBorder = minSizeCanvas * ratioWidthClockStroke                          //Ширина обводки часов
        val textSize = minSizeCanvas * ratioTextSize                                          //Размер шрифта для цифр
        paint.textSize = textSize

        //Отрисовка фона и обводки часов
        paint.color = colorBorderClock
        canvas.drawOval(deltaRadiusWidth, deltaRadiusHeight, canvas.width.toFloat()-deltaRadiusWidth, canvas.height.toFloat()-deltaRadiusHeight, paint)
        paint.color = colorBackgroundClock
        canvas.drawOval(deltaWidthBorder+deltaRadiusWidth, deltaWidthBorder+deltaRadiusHeight, canvas.width-deltaWidthBorder-deltaRadiusWidth, canvas.height-deltaWidthBorder-deltaRadiusHeight, paint)

        //Отрисовка отметок минутной и секундной стрелки
        paint.color = colorDot
        for (i in 0..59) {
            //Угол до отметки
            val angle = PI.toFloat() * (i / 30f - 0.5f)
            //Координаты отметки минутной/секундной стрелки с учетом отступа в 10%
            val xDot = deltaRadiusWidth + radius + cos(angle)*radius * (1f - ratioPaddingDot)
            val yDot = deltaRadiusHeight + radius + sin(angle)*radius * (1f - ratioPaddingDot)
            //Радиус отметки (для каждой пятой отметки - меняем радиус отметки и рисуем цифру)
            var radiusDot = minSizeCanvas * ratioRadiusSmallDot
            if (i%5==0) {
                paint.color = colorNumber
                radiusDot = minSizeCanvas * ratioRadiusBigDot
                val xNumber = deltaRadiusWidth + radius + cos(angle)*radius * (1f - ratioPaddingNumber)
                val yNumber = deltaRadiusHeight + radius + sin(angle)*radius * (1f - ratioPaddingNumber) - ((paint.descent() + paint.ascent()) / 2f)
                canvas.drawText(((i/5+11)%12+1).toString(), xNumber, yNumber, paint)
                paint.color = colorDot
            }

            canvas.drawCircle(xDot, yDot, radiusDot, paint)
        }
    }


    /**Отрисовка стрелок круглых часов (isCircle = true)
     * На вход принимает 1 параметр:
     *  canvas: Canvas? - холст, на котором необходимо выполнить отрисовку
     * Внутри происходит рисование стрелок по следующим правилам:
     *  Радиус секундной стрелки - ratioLengthSecondHand (85% по умолчанию) от радиуса часов + ratioLengthTailHand (20% по умолчанию) хвостик.
     *      Ширина стрелки - ratioWidthSecondHand (по умолчанию 1% от минимальных размеров View)
     *  Радиус минутной стрелки - ratioLengthMinuteHand (80% по умолчанию) от радиуса часов + ratioLengthTailHand (20% по умолчанию) хвостик.
     *      Ширина стрелки - ratioWidthMinuteHand (по умолчанию 2% от минимальных размеров View)
     *  Радиус часовой стрелки - ratioLengthHourHand (70% по умолчанию) от радиуса часов + ratioLengthTailHand (20% по умолчанию) хвостик.
     *      Ширина стрелки - ratioWidthHourHand (по умолчанию 3% от минимальных размеров View)*/
    private fun drawHandsCircleClock(canvas: Canvas) {
        //Получаем текущее время
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR)
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        //Рассчеты для отрисовки стрелок
        val minSizeCanvas = min(canvas.width, canvas.height)                                  //Минимальные размеры View
        val radius = minSizeCanvas / 2f                                                       //Радиус часов
        val deltaRadiusWidth = if (canvas.width>canvas.height) (canvas.width - canvas.height) / 2f else 0f  //Разница между полуосями овала по ширине (если отрицательна, то = 0)
        val deltaRadiusHeight = if (canvas.height>canvas.width) (canvas.height - canvas.width) / 2f else 0f //Разница между полуосями овала по высоте (если отрицательна, то = 0)


        //Отрисовка секундной стрелки
        paint.strokeWidth = minSizeCanvas * ratioWidthSecondHand
        paint.color = colorSecondHand
        val angleSecond = PI.toFloat() * (seconds / 30f - 0.5f) //Угол секундной стрелки
        val xStartSecondHand = deltaRadiusWidth + radius - cos(angleSecond)*radius*ratioLengthTailHand
        val yStartSecondHand = deltaRadiusHeight + radius - sin(angleSecond)*radius*ratioLengthTailHand
        val xStopSecondHand = radius + cos(angleSecond) * radius * ratioLengthSecondHand + deltaRadiusWidth
        val yStopSecondHand = radius + sin(angleSecond) * radius * ratioLengthSecondHand + deltaRadiusHeight
        canvas.drawLine(xStartSecondHand, yStartSecondHand, xStopSecondHand, yStopSecondHand, paint)

        //Отрисовка минутной стрелки
        paint.strokeWidth = minSizeCanvas * ratioWidthMinuteHand
        paint.color = colorMinuteHand
        val angleMinute = PI.toFloat() * (minutes / 30f - 0.5f) + (PI.toFloat() * seconds / 60f / 30f) //Угол минутной стрелки с учетом секундной стрелки
        val xStartMinuteHand = deltaRadiusWidth + radius - cos(angleMinute)*radius*ratioLengthTailHand
        val yStartMinuteHand = deltaRadiusHeight + radius - sin(angleMinute)*radius*ratioLengthTailHand
        val xStopMinuteHand = radius + cos(angleMinute) * radius * ratioLengthMinuteHand + deltaRadiusWidth
        val yStopMinuteHand = radius + sin(angleMinute) * radius * ratioLengthMinuteHand + deltaRadiusHeight
        canvas.drawLine(xStartMinuteHand, yStartMinuteHand, xStopMinuteHand, yStopMinuteHand, paint)

        //Отрисовка часовой стрелки
        paint.strokeWidth = minSizeCanvas * ratioWidthHourHand
        paint.color = colorHourHand
        val angleHour = PI.toFloat() / 2f * (hour % 12 / 3 - 1) + (PI.toFloat() * minutes / 60f / 6f) //Угол часовой стрелки с учетом минутной стрелки
        val xStartHourHand = deltaRadiusWidth + radius - cos(angleHour)*radius*ratioLengthTailHand
        val yStartHourHand = deltaRadiusHeight + radius - sin(angleHour)*radius*ratioLengthTailHand
        val xStopHourHand = radius + cos(angleHour) * radius * ratioLengthHourHand + deltaRadiusWidth
        val yStopHourHand = radius + sin(angleHour) * radius * ratioLengthHourHand + deltaRadiusHeight
        canvas.drawLine(xStartHourHand, yStartHourHand, xStopHourHand, yStopHourHand, paint)
    }

}
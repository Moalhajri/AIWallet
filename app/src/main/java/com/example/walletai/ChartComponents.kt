package com.example.walletai.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

sealed class ChartData {
    data class LineChartData(
        val points: List<Point>,
        val label: String,
        val maxY: Float = points.maxOfOrNull { it.y } ?: 0f,
        val minY: Float = points.minOfOrNull { it.y } ?: 0f
    ) {
        data class Point(val x: Float, val y: Float, val label: String)
    }

    data class BarChartData(
        val bars: List<Bar>,
        val maxValue: Float = bars.maxOfOrNull { it.value } ?: 0f
    ) {
        data class Bar(val value: Float, val label: String, val color: Color)
    }

    data class PieChartData(
        val segments: List<Segment>
    ) {
        data class Segment(val value: Float, val label: String, val color: Color)
    }
}

@Composable
fun SpendingTrendChart(
    data: ChartData.LineChartData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Spending Trend",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                val width = size.width
                val height = size.height
                val points = data.points

                if (points.isEmpty()) return@Canvas

                val xStep = width / (points.size - 1)
                val yRange = data.maxY - data.minY
                val yScale = height / yRange

                // Draw axes
                drawLine(
                    Color.Gray,
                    Offset(0f, height),
                    Offset(width, height),
                    strokeWidth = 1f
                )
                drawLine(
                    Color.Gray,
                    Offset(0f, 0f),
                    Offset(0f, height),
                    strokeWidth = 1f
                )

                // Draw line chart
                val path = Path()
                points.forEachIndexed { index, point ->
                    val x = index * xStep
                    val y = height - ((point.y - data.minY) * yScale)

                    if (index == 0) {
                        path.moveTo(x, y)
                    } else {
                        path.lineTo(x, y)
                    }

                    // Draw points
                    drawCircle(
                        Color.Blue,
                        radius = 4f,
                        center = Offset(x, y)
                    )
                }

                drawPath(
                    path,
                    Color.Blue,
                    style = Stroke(width = 2f)
                )
            }
        }
    }
}

@Composable
fun CategoryDistributionChart(
    data: ChartData.PieChartData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Spending by Category",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                val center = Offset(size.width / 2, size.height / 2)
                val radius = minOf(size.width, size.height) / 2.5f

                var startAngle = 0f
                val total = data.segments.sumOf { it.value.toDouble() }.toFloat()

                data.segments.forEach { segment ->
                    val sweepAngle = (segment.value / total) * 360f

                    drawArc(
                        color = segment.color,
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = true,
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )

                    startAngle += sweepAngle
                }
            }
        }
    }
}

@Composable
fun BudgetComparisonChart(
    data: ChartData.BarChartData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        ) {
            Text(
                text = "Budget vs Actual",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                val width = size.width
                val height = size.height
                val barWidth = width / (data.bars.size * 2)

                data.bars.forEachIndexed { index, bar ->
                    val x = (index * 2 + 1) * barWidth
                    val barHeight = (bar.value / data.maxValue) * height

                    drawRect(
                        color = bar.color,
                        topLeft = Offset(x, height - barHeight),
                        size = Size(barWidth, barHeight)
                    )
                }
            }
        }
    }
}
package com.github.cnrture.quickprojectwizard.toolwindow.manager.apitester

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.cnrture.quickprojectwizard.components.*
import com.github.cnrture.quickprojectwizard.theme.QPWTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlin.time.measureTime

@Composable
fun ApiTesterContent() {
    var selectedMethod by remember { mutableStateOf("GET") }
    var url by remember { mutableStateOf("https://api.canerture.com/harrypotterapp/characters") }
    var requestBody by remember { mutableStateOf("") }
    var responseText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var responseTime by remember { mutableStateOf(0L) }
    var statusCode by remember { mutableStateOf(0) }
    var selectedTab by remember { mutableStateOf("headers") }
    var headers by remember { mutableStateOf(mapOf("Content-Type" to "application/json")) }
    var queryParams by remember { mutableStateOf(mapOf<String, String>()) }

    val scope = rememberCoroutineScope()
    val methods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(QPWTheme.colors.black)
            .padding(24.dp)
    ) {
        QPWText(
            modifier = Modifier.fillMaxWidth(),
            text = "API Testing Tool",
            style = TextStyle(
                color = QPWTheme.colors.red,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = QPWTheme.colors.gray,
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    methods.forEach { method ->
                        QPWActionCard(
                            title = method,
                            icon = null,
                            actionColor = if (selectedMethod == method) QPWTheme.colors.red else QPWTheme.colors.lightGray,
                            type = QPWActionCardType.MEDIUM,
                            onClick = { selectedMethod = method }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QPWTextField(
                        modifier = Modifier.weight(1f),
                        value = url,
                        onValueChange = { url = it },
                        placeholder = "Enter API endpoint URL..."
                    )
                    QPWActionCard(
                        title = if (isLoading) "..." else "Send",
                        icon = if (isLoading) null else Icons.AutoMirrored.Rounded.Send,
                        actionColor = QPWTheme.colors.red,
                        type = QPWActionCardType.MEDIUM,
                        onClick = {
                            if (!isLoading) {
                                scope.launch {
                                    isLoading = true
                                    val result = makeApiRequest(selectedMethod, url, requestBody, headers, queryParams)
                                    responseText = result.first
                                    statusCode = result.second
                                    responseTime = result.third
                                    isLoading = false
                                }
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QPWTabRow(
                        text = "Headers",
                        isSelected = selectedTab == "headers",
                        color = QPWTheme.colors.red,
                        onTabSelected = { selectedTab = "headers" }
                    )
                    QPWTabRow(
                        text = "Query",
                        isSelected = selectedTab == "query",
                        color = QPWTheme.colors.red,
                        onTabSelected = { selectedTab = "query" }
                    )
                    QPWTabRow(
                        text = "Body",
                        isSelected = selectedTab == "body",
                        color = QPWTheme.colors.red,
                        onTabSelected = { selectedTab = "body" }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (selectedTab) {
                    "headers" -> HeadersSection(
                        headers = headers,
                        onHeadersChange = { headers = it }
                    )

                    "query" -> QueryParamsSection(
                        queryParams = queryParams,
                        onQueryParamsChange = { queryParams = it }
                    )

                    "body" -> BodySection(
                        body = requestBody,
                        onBodyChange = { requestBody = it },
                        method = selectedMethod
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = QPWTheme.colors.gray,
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QPWText(
                        text = "Response",
                        color = QPWTheme.colors.white,
                        style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (statusCode > 0) {
                            StatusCodeChip(statusCode)
                            QPWText(
                                text = "${responseTime}ms",
                                color = QPWTheme.colors.lightGray,
                                style = TextStyle(fontSize = 12.sp)
                            )
                        }

                        QPWActionCard(
                            title = "Copy",
                            icon = Icons.Rounded.ContentCopy,
                            actionColor = QPWTheme.colors.red,
                            type = QPWActionCardType.SMALL,
                            onClick = {
                                try {
                                    val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                                    clipboard.setContents(StringSelection(responseText), null)
                                } catch (_: Exception) {
                                }
                            }
                        )

                        QPWActionCard(
                            title = "Clear",
                            icon = Icons.Rounded.Clear,
                            actionColor = QPWTheme.colors.red,
                            type = QPWActionCardType.SMALL,
                            onClick = {
                                responseText = ""
                                statusCode = 0
                                responseTime = 0
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                QPWTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    value = responseText,
                    onValueChange = {},
                    isSingleLine = false,
                    placeholder = "Response will appear here..."
                )
            }
        }
    }
}

@Composable
private fun HeadersSection(
    headers: Map<String, String>,
    onHeadersChange: (Map<String, String>) -> Unit,
) {
    Column {
        QPWText(
            text = "Request Headers",
            color = QPWTheme.colors.white,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
        )

        Spacer(modifier = Modifier.height(12.dp))

        headers.forEach { (key, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QPWTextField(
                    modifier = Modifier.weight(1f),
                    value = key,
                    onValueChange = { newKey ->
                        val newHeaders = headers.toMutableMap()
                        newHeaders.remove(key)
                        newHeaders[newKey] = value
                        onHeadersChange(newHeaders)
                    },
                    placeholder = "Header name"
                )

                QPWTextField(
                    modifier = Modifier.weight(1f),
                    value = value,
                    onValueChange = { newValue ->
                        val newHeaders = headers.toMutableMap()
                        newHeaders[key] = newValue
                        onHeadersChange(newHeaders)
                    },
                    placeholder = "Header value"
                )

                QPWActionCard(
                    title = "×",
                    icon = null,
                    actionColor = QPWTheme.colors.red,
                    type = QPWActionCardType.SMALL,
                    onClick = {
                        val newHeaders = headers.toMutableMap()
                        newHeaders.remove(key)
                        onHeadersChange(newHeaders)
                    }
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        QPWActionCard(
            title = "Add Header",
            icon = Icons.Rounded.Add,
            actionColor = QPWTheme.colors.red,
            type = QPWActionCardType.SMALL,
            onClick = {
                val newHeaders = headers.toMutableMap()
                newHeaders[""] = ""
                onHeadersChange(newHeaders)
            }
        )
    }
}

@Composable
private fun QueryParamsSection(
    queryParams: Map<String, String>,
    onQueryParamsChange: (Map<String, String>) -> Unit,
) {
    Column {
        QPWText(
            text = "Query Parameters",
            color = QPWTheme.colors.white,
            style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
        )

        Spacer(modifier = Modifier.height(8.dp))

        queryParams.forEach { (key, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QPWTextField(
                    modifier = Modifier.weight(1f),
                    value = key,
                    onValueChange = { newKey ->
                        val newParams = queryParams.toMutableMap()
                        newParams.remove(key)
                        newParams[newKey] = value
                        onQueryParamsChange(newParams)
                    },
                    placeholder = "Query parameter name"
                )

                QPWTextField(
                    modifier = Modifier.weight(1f),
                    value = value,
                    onValueChange = { newValue ->
                        val newParams = queryParams.toMutableMap()
                        newParams[key] = newValue
                        onQueryParamsChange(newParams)
                    },
                    placeholder = "Query parameter value"
                )

                QPWActionCard(
                    title = "×",
                    icon = null,
                    actionColor = QPWTheme.colors.red,
                    type = QPWActionCardType.SMALL,
                    onClick = {
                        val newParams = queryParams.toMutableMap()
                        newParams.remove(key)
                        onQueryParamsChange(newParams)
                    }
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        QPWActionCard(
            title = "Add Query Param",
            icon = Icons.Rounded.Add,
            actionColor = QPWTheme.colors.red,
            type = QPWActionCardType.SMALL,
            onClick = {
                val newParams = queryParams.toMutableMap()
                newParams[""] = ""
                onQueryParamsChange(newParams)
            }
        )
    }
}

@Composable
private fun BodySection(
    body: String,
    onBodyChange: (String) -> Unit,
    method: String,
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            QPWText(
                text = "Request Body",
                color = QPWTheme.colors.white,
                style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium)
            )

            if (method in listOf("GET", "DELETE")) {
                QPWText(
                    text = "$method requests typically don't have a body",
                    color = QPWTheme.colors.red,
                    style = TextStyle(fontSize = 12.sp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        QPWTextField(
            modifier = Modifier.fillMaxWidth(),
            value = body,
            onValueChange = onBodyChange,
            isSingleLine = false,
            placeholder = "Enter request body (JSON, XML, etc.)..."
        )
    }
}

@Composable
private fun StatusCodeChip(statusCode: Int) {
    val color = when (statusCode) {
        in 200..299 -> QPWTheme.colors.green
        in 300..399 -> QPWTheme.colors.purple
        in 400..499 -> QPWTheme.colors.red
        in 500..599 -> QPWTheme.colors.red
        else -> QPWTheme.colors.lightGray
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        backgroundColor = color.copy(alpha = 0.2f),
        elevation = 0.dp
    ) {
        QPWText(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            text = statusCode.toString(),
            color = color,
            style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Bold)
        )
    }
}

private suspend fun makeApiRequest(
    method: String,
    urlString: String,
    body: String,
    headers: Map<String, String>,
    queryParams: Map<String, String>,
): Triple<String, Int, Long> = withContext(Dispatchers.IO) {
    var connection: HttpURLConnection? = null

    val time = measureTime {
        try {
            val url = URL(
                urlString + (if (queryParams.isNotEmpty()) {
                    buildQueryParamsString(queryParams)
                } else {
                    ""
                })
            )
            connection = url.openConnection() as HttpURLConnection

            connection.apply {
                requestMethod = method
                doOutput = method in listOf("POST", "PUT", "PATCH")
                connectTimeout = 20000
                readTimeout = 20000

                headers.forEach { (key, value) ->
                    if (key.isNotBlank() && value.isNotBlank()) {
                        setRequestProperty(key, value)
                    }
                }

                if (doOutput && body.isNotBlank()) {
                    val writer = OutputStreamWriter(outputStream)
                    writer.write(body)
                    writer.flush()
                    writer.close()
                }
            }
        } catch (e: Exception) {
            return@withContext Triple("Error: ${e.message}", 0, 0L)
        }
    }

    try {
        val statusCode = connection?.responseCode ?: 0
        val inputStream = if (statusCode >= 400) {
            connection?.errorStream
        } else {
            connection?.inputStream
        }

        val response = inputStream?.use { stream ->
            BufferedReader(InputStreamReader(stream)).use { reader ->
                reader.readText()
            }
        } ?: "No response"

        Triple(response, statusCode, time.inWholeMilliseconds)
    } catch (e: Exception) {
        Triple("Error reading response: ${e.message}", connection?.responseCode ?: 0, time.inWholeMilliseconds)
    } finally {
        connection?.disconnect()
    }
}

private fun buildQueryParamsString(params: Map<String, String>): String {
    if (params.isEmpty()) return ""

    val filteredParams = params.filterKeys { it.isNotBlank() }
        .filterValues { it.isNotBlank() }

    if (filteredParams.isEmpty()) return ""

    return "?" + filteredParams.map { "${encode(it.key)}=${encode(it.value)}" }
        .joinToString("&")
}

private fun encode(value: String): String {
    return java.net.URLEncoder.encode(value, "UTF-8")
}

package com.rupiksha.distributer.presentation.auth.login

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.ui.geometry.Offset
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rupiksha.distributer.R
import com.rupiksha.distributer.di.AppContainer
import com.rupiksha.distributer.ui.theme.*

@Composable
fun LoginScreen(
    appContainer: AppContainer,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    val viewModel: LoginViewModel = viewModel(factory = LoginViewModelFactory(appContainer))
    val uiState by viewModel.uiState.collectAsState()

    LoginContent(
        uiState = uiState,
        onLogin = { username, pin -> viewModel.login(username, pin) },
        onLoginSuccess = onLoginSuccess,
        onRegisterClick = onRegisterClick
    )
}

@Composable
fun LoginContent(
    uiState: LoginUiState,
    onLogin: (String, String) -> Unit,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableIntStateOf(1) } // 0: Sales, 1: Partner, 2: Partner Employee
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isAgreed by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    // Both fields filled = the button "wakes up"
    val isFormValid = username.isNotBlank() && password.isNotBlank()

    // Detects a failed login (was loading, finished loading, did not succeed)
    // and triggers a shake without needing to know the exact error field on uiState.
    var wasLoading by remember { mutableStateOf(false) }
    var shakeTrigger by remember { mutableIntStateOf(0) }
    val shakeOffset = remember { Animatable(0f) }

    LaunchedEffect(uiState.isLoading, uiState.success) {
        if (wasLoading && !uiState.isLoading && !uiState.success) {
            shakeTrigger++
        }
        wasLoading = uiState.isLoading
    }

    LaunchedEffect(shakeTrigger) {
        if (shakeTrigger > 0) {
            val keyframes = listOf(0f, -14f, 14f, -10f, 10f, -6f, 6f, -3f, 3f, 0f)
            for (x in keyframes) {
                shakeOffset.animateTo(x, animationSpec = tween(durationMillis = 45))
            }
        }
    }

    LaunchedEffect(uiState.success) {
        if (uiState.success) {
            onLoginSuccess()
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Logo Section
            val logoInfiniteTransition = rememberInfiniteTransition(label = "logoBorderAnim")
            val logoShimmer by logoInfiniteTransition.animateFloat(
                initialValue = -1f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
                label = "logoShimmer"
            )
            val logoTertiary = MaterialTheme.colorScheme.tertiary
            val logoBorderBrush = Brush.linearGradient(
                colors = listOf(
                    BrandPrimary.copy(alpha = 0.6f),
                    logoTertiary.copy(alpha = 0.5f),
                    BrandPrimaryDark.copy(alpha = 0.6f),
                    BrandPrimary.copy(alpha = 0.6f)
                ),
                start = Offset(logoShimmer * 220f, 0f),
                end = Offset(logoShimmer * 220f + 220f, 220f)
            )

            Box(
                modifier = Modifier
                    .size(150.dp)
                    .shadow(12.dp, CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .border(3.dp, logoBorderBrush, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Rupiksha Logo",
                    modifier = Modifier
                        .size(150.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Welcome Header
            Text(
                text = "Welcome,",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    letterSpacing = (-0.5).sp
                )
            )
            Text(
                text = "Please sign in to continue",
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Role Selector
            RoleSegmentedControl(
                selectedRole = selectedRole,
                onRoleSelected = { selectedRole = it }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Input Fields Column
            Column(modifier = Modifier.fillMaxWidth()) {
                // Username label
                Text(
                    text = "USERNAME",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomOutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    placeholder = "Enter Your Username",
                    leadingIcon = Icons.Default.Person
                )

                Spacer(modifier = Modifier.height(20.dp))

                // PIN label
                Text(
                    text = "SECURE PIN",
                    style = TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                CustomOutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "Enter Your PIN",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isPasswordVisible = isPasswordVisible,
                    onPasswordToggle = { isPasswordVisible = !isPasswordVisible },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Forget login ID or Password?
            Row(
                modifier = Modifier
                    .clickable { /* Handle forget */ }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Forget login ID or Password?",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Agreement Section
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isAgreed) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .border(
                                1.dp,
                                if (isAgreed) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { isAgreed = !isAgreed },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isAgreed) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    val annotatedString = buildAnnotatedString {
                        append("I agree to the ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)) {
                            append("Terms & Conditions")
                        }
                        append(" and ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)) {
                            append("Privacy Policy")
                        }
                    }

                    Text(
                        text = annotatedString,
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 20.sp
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Log In Button — "wakes up" once both fields are filled, shakes on a failed attempt
            val isReadyToSubmit = isFormValid && isAgreed
            val buttonScale by animateFloatAsState(
                targetValue = if (isReadyToSubmit) 1f else 0.97f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "buttonScale"
            )
            val buttonDim by animateFloatAsState(
                targetValue = if (isReadyToSubmit) 1f else 0.5f,
                animationSpec = tween(durationMillis = 300),
                label = "buttonDim"
            )
            val buttonShadowElevation by animateDpAsState(
                targetValue = if (isReadyToSubmit) 10.dp else 2.dp,
                animationSpec = tween(durationMillis = 300),
                label = "buttonShadowElevation"
            )

            Button(
                onClick = { onLogin(username, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .graphicsLayer {
                        scaleX = buttonScale
                        scaleY = buttonScale
                        translationX = shakeOffset.value
                    }
                    .shadow(buttonShadowElevation, RoundedCornerShape(12.dp)),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(),
                enabled = !uiState.isLoading && isReadyToSubmit
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = buttonDim }
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(BrandPrimary, BrandPrimaryDark)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "Log In",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Registration Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    style = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                )
                Text(
                    text = "Register",
                    modifier = Modifier.clickable { onRegisterClick() },
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // Footer
            HorizontalDivider(
                Modifier,
                DividerDefaults.Thickness,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Copyright,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "RUPIKSHA",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "System Version 1.0.0",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    isPassword: Boolean = false,
    isPasswordVisible: Boolean = false,
    onPasswordToggle: () -> Unit = {},
    isValid: Boolean = value.isNotBlank(),
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        val isError = errorMessage != null
        val interactionSource = remember { MutableInteractionSource() }
        val isFocused by interactionSource.collectIsFocusedAsState()

        // Every field carries a live, colourful gradient border rather than a flat grey
        // outline — a soft brand-coloured wash at rest, blooming into a richer moving
        // gradient the moment it's focused or filled, and swapping to red on error.
        val activeLevel by animateFloatAsState(
            targetValue = when {
                isError -> 0f
                isFocused -> 1f
                isValid -> 0.6f
                else -> 0.22f
            },
            animationSpec = tween(durationMillis = 280, easing = FastOutSlowInEasing),
            label = "fieldActiveLevel"
        )
        val borderWidth by animateDpAsState(
            targetValue = if (isFocused || isError) 2.2.dp else if (isValid) 1.8.dp else 1.4.dp,
            animationSpec = tween(durationMillis = 180),
            label = "fieldBorderWidth"
        )
        val iconColor by animateColorAsState(
            targetValue = when {
                isError -> MaterialTheme.colorScheme.error
                isFocused || isValid -> BrandPrimary
                else -> MaterialTheme.colorScheme.onSurfaceVariant
            },
            animationSpec = tween(durationMillis = 220),
            label = "fieldIconColor"
        )
        val fieldScale by animateFloatAsState(
            targetValue = if (isFocused) 1.012f else 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "fieldFocusScale"
        )

        // A gentle, continuous shimmer sweeps the gradient stops back and forth so an
        // otherwise static border always feels alive, not just on focus.
        val infiniteTransition = rememberInfiniteTransition(label = "fieldGradientAnim")
        val shimmerShift by infiniteTransition.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(tween(2600, easing = LinearEasing)),
            label = "shimmerShift"
        )
        val pulse by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                tween(1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "fieldPulse"
        )

        // Quick left-right shake the instant an error appears, so it reads as a nudge
        // rather than a silent colour swap.
        val shakeOffset = remember { Animatable(0f) }
        LaunchedEffect(isError) {
            if (isError) {
                shakeOffset.snapTo(0f)
                listOf(9f, -7f, 5f, -3f, 0f).forEach { target ->
                    shakeOffset.animateTo(target, animationSpec = tween(55))
                }
            }
        }

        val tertiary = MaterialTheme.colorScheme.tertiary
        val errorAccent = Color(0xFFFF7A45)
        val gradientColors = when {
            isError -> listOf(
                MaterialTheme.colorScheme.error,
                errorAccent,
                MaterialTheme.colorScheme.error,
                MaterialTheme.colorScheme.error.copy(alpha = 0.75f)
            )
            else -> listOf(
                BrandPrimary.copy(alpha = 0.35f + activeLevel * 0.65f),
                tertiary.copy(alpha = 0.28f + activeLevel * 0.5f),
                BrandPrimaryDark.copy(alpha = 0.35f + activeLevel * 0.65f),
                BrandPrimary.copy(alpha = 0.35f + activeLevel * 0.65f)
            )
        }
        val borderBrush = Brush.linearGradient(
            colors = gradientColors,
            start = Offset(shimmerShift * 240f, 0f),
            end = Offset(shimmerShift * 240f + 240f, 220f)
        )
        val glowPrimary = if (isError) MaterialTheme.colorScheme.error else BrandPrimary
        val glowSecondary = if (isError) errorAccent else tertiary
        val glowBaseAlpha = if (isError) 0.22f else (0.05f + activeLevel * 0.16f)
        val glowAlpha = glowBaseAlpha * (0.7f + pulse * 0.3f)

        Box {
            // Soft colourful glow blooming behind the field — present even at rest so
            // every box on the step reads as "alive", stronger while focused.
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(2.dp)
                    .background(
                        Brush.radialGradient(
                            listOf(
                                glowPrimary.copy(alpha = glowAlpha),
                                glowSecondary.copy(alpha = glowAlpha * 0.7f),
                                Color.Transparent
                            )
                        ),
                        RoundedCornerShape(16.dp)
                    )
            )

            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = fieldScale
                        scaleY = fieldScale
                        translationX = shakeOffset.value
                    }
                    .border(borderWidth, borderBrush, RoundedCornerShape(14.dp)),
                isError = isError,
                placeholder = {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = iconColor,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = if (isPassword) {
                    {
                        IconButton(onClick = onPasswordToggle) {
                            Icon(
                                imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null,
                                tint = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    {
                        androidx.compose.animation.AnimatedVisibility(
                            visible = isValid && !isError,
                            enter = scaleIn(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) + fadeIn(),
                            exit = scaleOut() + fadeOut()
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = BrandPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        if (isError) {
                            Icon(
                                imageVector = Icons.Default.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                },
                visualTransformation = if (isPassword && !isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                keyboardOptions = keyboardOptions,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    errorBorderColor = Color.Transparent,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                ),
                singleLine = true,
                textStyle = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        AnimatedVisibility(
            visible = isError,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun RoleSegmentedControl(
    selectedRole: Int,
    onRoleSelected: (Int) -> Unit
) {
    val roles = listOf("Sales /\nPromoter", "Partner", "Partner\nEmployee")

    val roleInfiniteTransition = rememberInfiniteTransition(label = "roleBorderAnim")
    val roleShimmer by roleInfiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing)),
        label = "roleShimmer"
    )
    val roleTertiary = MaterialTheme.colorScheme.tertiary
    val roleBorderBrush = Brush.linearGradient(
        colors = listOf(
            BrandPrimary.copy(alpha = 0.55f),
            roleTertiary.copy(alpha = 0.45f),
            BrandPrimaryDark.copy(alpha = 0.55f),
            BrandPrimary.copy(alpha = 0.55f)
        ),
        start = Offset(roleShimmer * 340f, 0f),
        end = Offset(roleShimmer * 340f + 340f, 340f)
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.6.dp, roleBorderBrush),
        shadowElevation = 2.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            val segmentWidth = maxWidth / roles.size

            // Sliding "pill" indicator that glides to the selected segment
            val indicatorOffset by animateDpAsState(
                targetValue = segmentWidth * selectedRole,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                ),
                label = "roleIndicatorOffset"
            )

            Box(
                modifier = Modifier
                    .offset(x = indicatorOffset)
                    .width(segmentWidth)
                    .fillMaxHeight()
                    .shadow(4.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.primary)
            )

            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                roles.forEachIndexed { index, role ->
                    val isSelected = selectedRole == index

                    // Text color and a subtle scale "pop" animate alongside the slide
                    val textColor by animateColorAsState(
                        targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        animationSpec = tween(durationMillis = 200),
                        label = "roleTextColor"
                    )
                    val scale by animateFloatAsState(
                        targetValue = if (isSelected) 1f else 0.94f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessLow
                        ),
                        label = "roleTextScale"
                    )

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(32.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            ) { onRoleSelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = role,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.graphicsLayer(scaleX = scale, scaleY = scale),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                                color = textColor,
                                lineHeight = 14.sp
                            )
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        LoginContent(
            uiState = LoginUiState(),
            onLogin = { _, _ -> },
            onLoginSuccess = {},
            onRegisterClick = {}
        )
    }
}
package com.optic.cinema.fragments.settings

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.StateListDrawable
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import com.optic.cinema.R
import com.optic.cinema.utils.ThemeManager
import com.optic.cinema.utils.UserPreferences

internal object SettingsListStyler {
    private data class DefaultRowStyle(
        val background: Drawable?,
        val minHeight: Int,
        val paddingLeft: Int,
        val paddingTop: Int,
        val paddingRight: Int,
        val paddingBottom: Int,
        val marginLeft: Int,
        val marginTop: Int,
        val marginRight: Int,
        val marginBottom: Int,
        val titleColor: Int,
        val titleSizePx: Float,
        val summaryColor: Int?,
        val summarySizePx: Float?,
    )

    fun attach(root: View, isTv: Boolean) {
        val recyclerView = findRecyclerView(root) ?: return
        if (recyclerView.getTag(R.id.settings_list_styler_tag) == true) return

        val backgroundColor = resolveThemeColor(root, R.attr.app_background_color, 0xFF181818.toInt())
        root.setBackgroundColor(backgroundColor)
        recyclerView.setTag(R.id.settings_list_styler_tag, true)
        recyclerView.clipToPadding = false
        recyclerView.setBackgroundColor(backgroundColor)
        recyclerView.setPadding(
            0,
            recyclerView.context.dp(if (isTv) 18 else 10),
            0,
            recyclerView.context.dp(if (isTv) 28 else 18),
        )

        recyclerView.addOnChildAttachStateChangeListener(object : RecyclerView.OnChildAttachStateChangeListener {
            override fun onChildViewAttachedToWindow(view: View) = styleRow(view, isTv)
            override fun onChildViewDetachedFromWindow(view: View) {
                view.clearAnimation() // Prevent memory leaks
            }
        })

        recyclerView.adapter?.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = restyle()
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) = restyle()
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) = restyle()
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = restyle()
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = restyle()
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) = restyle()

            private fun restyle() {
                recyclerView.post { styleVisibleRows(recyclerView, isTv) }
            }
        })

        recyclerView.post { styleVisibleRows(recyclerView, isTv) }
    }

    private fun styleVisibleRows(recyclerView: RecyclerView, isTv: Boolean) {
        for (index in 0 until recyclerView.childCount) {
            styleRow(recyclerView.getChildAt(index), isTv)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun styleRow(view: View, isTv: Boolean) {
        val title = view.findViewById<TextView>(android.R.id.title) ?: return
        val summary = view.findViewById<TextView>(android.R.id.summary)
        val icon = view.findViewById<ImageView>(android.R.id.icon)
        val chevron = view.findViewById<View>(R.id.settings_chevron)
        val layoutParams = view.layoutParams as? ViewGroup.MarginLayoutParams
        val context = view.context

        val defaults = (view.getTag(R.id.settings_row_defaults) as? DefaultRowStyle) ?: DefaultRowStyle(
            background = view.background,
            minHeight = view.minimumHeight,
            paddingLeft = view.paddingLeft,
            paddingTop = view.paddingTop,
            paddingRight = view.paddingRight,
            paddingBottom = view.paddingBottom,
            marginLeft = layoutParams?.leftMargin ?: 0,
            marginTop = layoutParams?.topMargin ?: 0,
            marginRight = layoutParams?.rightMargin ?: 0,
            marginBottom = layoutParams?.bottomMargin ?: 0,
            titleColor = title.currentTextColor,
            titleSizePx = title.textSize,
            summaryColor = summary?.currentTextColor,
            summarySizePx = summary?.textSize,
        ).also { view.setTag(R.id.settings_row_defaults, it) }

        val hasChevron = chevron != null
        if (!hasChevron) {
            // Revert to default logic... (kept same as your original)
            view.background = defaults.background
            return
        }

        val palette = ThemeManager.palette(UserPreferences.selectedTheme)
        val titleColor = palette.tvHeaderPrimary
        val summaryColor = palette.tvHeaderSecondary
        val accentColor = palette.mobileNavActive
        
        // 💎 Premium Frosted Glass Effect Colors
        val rowBackgroundColor = Color.parseColor("#12FFFFFF") // Slightly softer base
        val rowBorderColor = Color.parseColor("#2AFFFFFF")
        val rowHighlightColor = ColorUtils.blendARGB(rowBackgroundColor, accentColor, 0.25f)
        val rowHighlightBorderColor = ColorUtils.blendARGB(rowBorderColor, accentColor, 0.6f)

        layoutParams?.setMargins(
            context.dp(if (isTv) 28 else 20),
            context.dp(if (isTv) 8 else 8),
            context.dp(if (isTv) 28 else 20),
            context.dp(if (isTv) 8 else 8),
        )
        view.layoutParams = layoutParams
        view.background = createRowBackground(
            view = view,
            isTv = isTv,
            defaultColor = rowBackgroundColor,
            defaultStrokeColor = rowBorderColor,
            activeColor = rowHighlightColor,
            activeStrokeColor = rowHighlightBorderColor,
            rippleColor = accentColor
        )
        view.minimumHeight = context.dp(if (isTv) 88 else 72)
        view.setPadding(
            context.dp(if (isTv) 28 else 20),
            context.dp(if (isTv) 18 else 16),
            context.dp(if (isTv) 28 else 20),
            context.dp(if (isTv) 18 else 16),
        )

        title.setTextColor(titleColor)
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isTv) 20f else 16f)
        try { title.typeface = ResourcesCompat.getFont(context, R.font.rabar) } catch (e: Exception) { }

        summary?.apply {
            setTextColor(summaryColor)
            setTextSize(TypedValue.COMPLEX_UNIT_SP, if (isTv) 14f else 13f)
            maxLines = 2
            try { typeface = ResourcesCompat.getFont(context, R.font.rabar) } catch (e: Exception) { }
        }

        icon?.let {
            it.imageTintList = ColorStateList.valueOf(accentColor)
        }

        // 🚀 ADD MODERN ANIMATIONS HERE 🚀
        if (isTv) {
            view.setOnFocusChangeListener { v, hasFocus ->
                v.animate()
                    .scaleX(if (hasFocus) 1.04f else 1.0f)
                    .scaleY(if (hasFocus) 1.04f else 1.0f)
                    .translationZ(if (hasFocus) 10f else 0f) // Add floating shadow
                    .setDuration(250)
                    .setInterpolator(OvershootInterpolator(1.5f))
                    .start()

                // Animate chevron horizontally slightly
                chevron?.animate()
                    ?.translationX(if (hasFocus) 10f else 0f)
                    ?.setDuration(250)
                    ?.setInterpolator(OvershootInterpolator())
                    ?.start()
            }
        } else {
            // Mobile Bouncy Touch Feedback
            view.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.animate()
                            .scaleX(0.97f).scaleY(0.97f) // Compress in
                            .setDuration(150)
                            .setInterpolator(DecelerateInterpolator())
                            .start()
                    }
                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        v.animate()
                            .scaleX(1.0f).scaleY(1.0f) // Bounce back
                            .setDuration(300)
                            .setInterpolator(OvershootInterpolator(2f))
                            .start()
                    }
                }
                false // Important: Return false so OnClickListener still triggers!
            }
        }
    }

    private fun createRowBackground(
        view: View,
        isTv: Boolean,
        defaultColor: Int,
        defaultStrokeColor: Int,
        activeColor: Int,
        activeStrokeColor: Int,
        rippleColor: Int
    ): Drawable {
        val radiusDp = if (isTv) 26 else 22 
        val defaultStrokeDp = 1
        val activeStrokeDp = if (isTv) 2 else 1

        val defaultShape = createGradientRoundedRect(view, defaultColor, defaultStrokeColor, radiusDp, defaultStrokeDp)
        val activeShape = createGradientRoundedRect(view, activeColor, activeStrokeColor, radiusDp, activeStrokeDp)

        return if (isTv) {
            // TV: Needs StateListDrawable because D-Pad focus holds the state
            StateListDrawable().apply {
                addState(intArrayOf(android.R.attr.state_focused), activeShape)
                addState(intArrayOf(android.R.attr.state_selected), activeShape)
                addState(intArrayOf(), defaultShape)
            }
        } else {
            // Mobile: Needs RippleDrawable for that modern Google/Apple satisfying touch
            val colorStateList = ColorStateList(
                arrayOf(intArrayOf()), 
                intArrayOf(ColorUtils.setAlphaComponent(rippleColor, 80)) // 30% opacity ripple
            )
            // RippleDrawable(color, content, mask)
            RippleDrawable(colorStateList, defaultShape, defaultShape) 
        }
    }

    // 🌟 Upgrade: Using GradientDrawable with TL_BR to simulate light hitting glass
    private fun createGradientRoundedRect(
        view: View,
        baseColor: Int,
        strokeColor: Int,
        radiusDp: Int,
        strokeDp: Int,
    ): Drawable = GradientDrawable(
        GradientDrawable.Orientation.TL_BR,
        intArrayOf(
            ColorUtils.blendARGB(baseColor, Color.WHITE, 0.05f), // Top left slightly brighter
            baseColor // Bottom right base
        )
    ).apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = view.context.dp(radiusDp).toFloat()
        setStroke(view.context.dp(strokeDp), strokeColor)
    }

    private fun resolveThemeColor(view: View, attr: Int, fallback: Int): Int {
        val typedValue = TypedValue()
        return if (view.context.theme.resolveAttribute(attr, typedValue, true)) {
            if (typedValue.resourceId != 0) {
                androidx.core.content.ContextCompat.getColor(view.context, typedValue.resourceId)
            } else {
                typedValue.data
            }
        } else fallback
    }

    private fun findRecyclerView(view: View): RecyclerView? {
        if (view is RecyclerView) return view
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) {
                findRecyclerView(view.getChildAt(index))?.let { return it }
            }
        }
        return null
    }

    private fun android.content.Context.dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}

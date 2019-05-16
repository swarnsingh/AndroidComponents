package com.swarn.androidcomponents.transition

/**
 * @author Swarn Singh.
 */
enum class DetectedActivity(var value: Int) {
    IN_VEHICLE(0),
    ON_BICYCLE(1),
    ON_FOOT(2),
    STILL(3),
    UNKNOWN(4),
    TILTING(5),
    INVALID(6),
    WALKING(7),
    RUNNING(8)
}
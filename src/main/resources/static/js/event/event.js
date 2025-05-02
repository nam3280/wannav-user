$(document).ready(function () {
    $('#getCouponButton').click(function () {
        $.ajax({
            url: `/api/event/coupon`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({
                eventId: eventId,
                couponId: couponId,
            }),
            success: function (response) {
                alert(response);
            },
            error: function (xhr, status, error) {
                alert(xhr.responseText);
            }
        });
    });
});
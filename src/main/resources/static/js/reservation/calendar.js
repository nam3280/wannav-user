$(function() {
    moment.locale('ko');

    rome(inline_cal, { time: false, inputFormat: 'YYYY-MM-DD',dateValidator: rome.val.afterEq(moment().toDate()),}).on('data', function(value) {
        selectedDate = value;

        $("#person-buttons").empty();
        $("#reservation-guest").empty();
        $(".rectangle-button").removeClass('selected');
        selectedTime = '';
        selectedGuest = '';

        fetchReservationData(selectedDate, restaurantId);
    });


    function fetchReservationData(date, restaurantId) {
        $.ajax({
            url: `/api/reservation/date`,
            type: 'GET',
            data: { selectDate: date, restaurantId: restaurantId },
            success: function(response) {
                $("#reservation-time").html('<h2 style="padding-top: 40px;">예약 시간</h2>');

                let timeButton = '';
                let currentDateTime = new Date();
                let formattedDate = currentDateTime.toLocaleDateString('en-CA'); // 'YYYY-MM-DD' 형식

                if (response.reservationTimes.length === 0 || formattedDate > response.reservationDate)
                    timeButton = '<h5 style="font-size: 15px">예약할 수 없습니다.</h5>';
                else {
                    response.reservationTimes.forEach(function(time) {
                        if(formattedDate <= selectedDate)
                            timeButton += `<button type="button" class="rectangle-button" data-time="${time}">${time}</button>`;
                        else
                            timeButton += `<button type="button" class="rectangle-button" data-time="${time}" style="display: none">${time}</button>`;
                    });
                }

                $("#time-buttons").replaceWith(`<div class="button-slider" id="time-buttons">${timeButton}</div>`);

                if (selectedTime)
                    $(`.rectangle-button[data-time="${selectedTime}"]`).addClass('selected');
            },
            error: function(error) {
                console.error('AJAX 요청 실패:', error);
            }
        });
    }

    $(document).on('click', '.rectangle-button', function() {
        selectedGuest = '';

        selectedTime = $(this).data('time');

        $(".rectangle-button").removeClass('selected');
        $(this).addClass('selected');

        $.ajax({
            url: '/api/reservation/time',
            type: 'GET',
            data: {
                selectDate: selectedDate,
                selectTime: selectedTime,
                restaurantId: restaurantId
            },
            success: function(response) {
                $("#reservation-guest").html('<h2 style="padding-top: 40px;">인원 수</h2>');
                let guestAccount = response.guestAccount;

                if(guestAccount > 8)
                    guestAccount = 8;

                let personButton = '';

                if (guestAccount === 0)
                    personButton = '<h5 style="font-size: 15px;">예약할 수 없습니다.</h5>';
                else {
                    for (let i = 1; i <= guestAccount; i++)
                        personButton += `<button type="button" class="circle-button">${i}</button>`;
                }

                $("#person-buttons").html(personButton);
            },
            error: function(error) {
                console.error('AJAX 요청 실패:', error);
            }
        });
    });

    $(document).on('click', '.circle-button', function() {
        selectedGuest = $(this).text();
        if (selectedTime) {
            $(".rectangle-button").removeClass('selected');
            $(`.rectangle-button[data-time="${selectedTime}"]`).addClass('selected');
        }
    });

    $('#submitReservation').on('click', function(e) {
        $.ajax({
            url: '/api/reservation/confirm',
            type: 'POST',
            data: {
                selectDate: selectedDate,
                selectTime: selectedTime,
                restaurantId: restaurantId,
                selectGuest: selectedGuest
            },
            success: function(response) {
                if (response.status === 'success')
                    window.location.href = '/checkout/success';
                else if (response.status === 'payment') {
                    let url = `/checkout/reservation?selectDate=${selectedDate}&selectTime=${selectedTime}&restaurantId=${restaurantId}&selectGuest=${selectedGuest}`;
                    window.location.href = url.replace(/ /g,"");
                }
                else
                    alert(response.message);
            },
            error: function(xhr, status, error) {
                alert(xhr.responseJSON?.message || "오류가 발생했습니다. 다시 시도해주세요.");
            }
        });
    });
});
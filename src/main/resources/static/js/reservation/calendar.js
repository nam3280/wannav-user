let selectedDate = '';
let selectedTime = '';
let selectedGuest = '';
let isPenalty;

document.addEventListener('DOMContentLoaded', () => {
    moment.locale('ko');

    rome(inline_cal, {
        time: false,
        inputFormat: 'YYYY-MM-DD',
        dateValidator: rome.val.afterEq(moment().toDate()),
    }).on('data', async (value) => {
        selectedDate = value;

        document.getElementById("person-buttons").innerHTML = '';
        document.getElementById("reservation-guest").innerHTML = '';
        document.querySelectorAll(".rectangle-button").forEach(btn => btn.classList.remove('selected'));
        selectedTime = '';
        selectedGuest = '';

        await fetchReservationData(selectedDate, restaurantId);
    });

    document.addEventListener('click', async (e) => {
        if (e.target.matches('.rectangle-button')) {
            selectedTime = e.target.dataset.time;
            selectedGuest = '';
            document.querySelectorAll('.rectangle-button').forEach(btn => btn.classList.remove('selected'));
            e.target.classList.add('selected');

            await fetchGuestData();
        }

        if (e.target.matches('.circle-button')) {
            selectedGuest = e.target.textContent;
            if (selectedTime) {
                document.querySelectorAll(".rectangle-button").forEach(btn => btn.classList.remove('selected'));
                const timeBtn = document.querySelector(`.rectangle-button[data-time="${selectedTime}"]`);
                if (timeBtn) timeBtn.classList.add('selected');
            }
        }

        if (e.target.id === 'submitReservation') {
            await submitReservation();
        }
    });
});

async function fetchReservationData(date, restaurantId) {
    try {
        const res = await axios.get('/api/reservation/date', {
            params: { selectDate: date, restaurantId }
        });

        const response = res.data;
        document.getElementById("reservation-time").innerHTML = '<h2 style="padding-top: 40px;">예약 시간</h2>';

        let timeButton = '';
        const currentDateTime = new Date();
        const formattedDate = currentDateTime.toLocaleDateString('en-CA');

        if (response.reservationTimes.length === 0 || formattedDate > response.reservationDate) {
            timeButton = '<h5 style="font-size: 15px">예약할 수 없습니다.</h5>';
        } else {
            response.reservationTimes.forEach((time) => {
                if (formattedDate <= selectedDate) {
                    timeButton += `<button type="button" class="rectangle-button" data-time="${time}">${time}</button>`;
                } else {
                    timeButton += `<button type="button" class="rectangle-button" data-time="${time}" style="display: none">${time}</button>`;
                }
            });
        }

        const container = document.getElementById('time-buttons');
        container.innerHTML = timeButton;

        if (selectedTime) {
            const selectedBtn = document.querySelector(`.rectangle-button[data-time="${selectedTime}"]`);
            if (selectedBtn) selectedBtn.classList.add('selected');
        }
    } catch (error) {
        console.error('예약 시간 불러오기 실패:', error);
    }
}

async function fetchGuestData() {
    try {
        const res = await axios.get('/api/reservation/time', {
            params: {
                selectDate: selectedDate,
                selectTime: selectedTime,
                restaurantId: restaurantId
            }
        });

        isPenalty = res.data.isPenalty;

        const guestAccount = Math.min(res.data.guestAccount, 8);
        const container = document.getElementById("reservation-guest");
        container.innerHTML = '<h2 style="padding-top: 40px;">인원 수</h2>';

        if (guestAccount === 0) {
            document.getElementById("person-buttons").innerHTML = '<h5 style="font-size: 15px;">예약할 수 없습니다.</h5>';
        } else {
            let personButtons = '';
            for (let i = 1; i <= guestAccount; i++) {
                personButtons += `<button type="button" class="circle-button">${i}</button>`;
            }
            document.getElementById("person-buttons").innerHTML = personButtons;
        }


    } catch (error) {
        console.error('인원 수 불러오기 실패:', error);
    }
}

async function submitReservation() {
    try {
        console.log('selectDate:', selectedDate);
        console.log('selectTime:', selectedTime);
        console.log('restaurantId:', restaurantId);
        console.log('selectGuest:', selectedGuest);
        console.log('isPenalty:', isPenalty);

        const res = await axios.post('/api/reservation/confirm', {
            selectDate: selectedDate,
            selectTime: selectedTime,
            restaurantId: restaurantId,
            selectGuest: selectedGuest,
            isPenalty: isPenalty
        });

        const response = res.data;
        if (response.status === 'success') {
            window.location.href = '/checkout/success';
        } else if (response.status === 'payment') {
            const url = `/checkout/reservation?selectDate=${selectedDate}&selectTime=${selectedTime}&restaurantId=${restaurantId}&selectGuest=${selectedGuest}`;
            window.location.href = url.replace(/ /g, '');
        } else {
            alert(response.message);
        }
    } catch (error) {
        alert(error.response?.data?.message || "오류가 발생했습니다. 다시 시도해주세요.");
    }
}

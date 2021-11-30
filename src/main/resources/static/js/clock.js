const inc = 1000;

clock();

function clock() {
    const date = new Date();

    const hours = ((date.getHours() + 23) % 24 + 1);
    const minutes = date.getMinutes();
    const seconds = date.getSeconds();

    const hour = hours * 30;
    const minute = minutes * 6;
    const second = seconds * 6;

    document.querySelector('.hour').style.transform = `rotate(${hour + minute/60}deg)`
    document.querySelector('.minute').style.transform = `rotate(${minute + second/60}deg)`
    document.querySelector('.second').style.transform = `rotate(${second}deg)`

    $(".hours").html(( hours < 10 ? "0" : "" ) + hours);
    $(".min").html(( minutes < 10 ? "0" : "" ) + minutes);
    $(".sec").html(( seconds < 10 ? "0" : "" ) + seconds);
}

setInterval(clock, inc);

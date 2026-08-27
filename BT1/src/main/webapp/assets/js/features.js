// Features for horoscope, fortune, lottery, auspicious hours
(function(){
  function $(s){return document.querySelector(s)}

  // Horoscope
  var zodiac = $('#zodiac');
  if(zodiac){
    var messages = [
      'Hôm nay là ngày tốt để bắt đầu công việc mới.',
      'Hãy chú ý tới các mối quan hệ gia đình.',
      'Cơ hội tài chính có thể đến bất ngờ, cân nhắc kỹ.',
      'Sức khỏe ổn định, nhưng đừng quên nghỉ ngơi.',
      'Tránh lời hứa vội vàng; suy nghĩ trước khi nói.'
    ];
    zodiac.addEventListener('change', function(){
      var sign = zodiac.value;
      if(!sign){ $('#horoscopeResult').textContent = ''; return; }
      var pick = messages[Math.floor(Math.random()*messages.length)];
      $('#horoscopeResult').innerHTML = '<strong>' + sign + ':</strong> ' + pick;
    });
  }

  // Fortune
  var fortuneBtn = $('#fortuneBtn');
  if(fortuneBtn){
    var fortunes = [
      'Hôm nay bạn sẽ gặp may mắn nhỏ. Nên giữ thái độ khiêm tốn.',
      'Coi chừng cạm bẫy tài chính, đừng đầu tư bừa.',
      'Một người bạn cũ sẽ mang tới tin vui.',
      'Thời điểm tốt để học hỏi kỹ năng mới.',
      'Hãy dành thời gian cho bản thân và nghỉ ngơi.'
    ];
    fortuneBtn.addEventListener('click', function(){
      $('#fortuneResult').textContent = fortunes[Math.floor(Math.random()*fortunes.length)];
    });
  }

  // Lottery
  var lotteryBtn = $('#lotteryBtn');
  if(lotteryBtn){
    lotteryBtn.addEventListener('click', function(){
      var nums = [];
      while(nums.length < 6){
        var n = Math.floor(Math.random()*45) + 1;
        if(nums.indexOf(n) === -1) nums.push(n);
      }
      nums.sort(function(a,b){return a-b});
      $('#lotteryResult').textContent = nums.join(' - ');
    });
  }

  // Auspicious hours (simple mock: three good windows)
  var ausp = $('#auspiciousResult');
  if(ausp){
    var now = new Date();
    var options = { hour: '2-digit', minute: '2-digit' };
    var windows = ['07:00 - 09:00','11:00 - 13:00','17:00 - 19:00'];
    var html = windows.map(function(w){ return '<div>'+w+'</div>'; }).join('');
    ausp.innerHTML = html;
  }
})();
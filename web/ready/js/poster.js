
$(function() {
	
	console.log("ready"); // 처음 메인 페이지 왔을 때 바로 실행됨.

	// $(".fff").click(function(){
	//     // this.toggleClass("bc-selected");
	//     console.log("fffff");
	//     // this.data("cid")~~~
	// });


	$( "section" ).delegate( ".select-for-poster", "click", function(e) {   // 그래서 event delegate가 필요함. 그런데 지금보니 index.js에 posterController 실행될때 js로 써도됨.
  		$( this ).toggleClass( "chosen" ); // 카드를 선택할 때마다 'chosen'이라는 토글 클래스 추가/제거 

	});



	// $('div.tumble').toggleClass('bounce'


});
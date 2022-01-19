package springboard.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import springboard.command.BbsCommandImpl;
import springboard.command.ListCommand;
import springboard.command.WriteActionCommand;
import springboard.model.JdbcTemplateConst;
import springboard.model.SpringBbsDTO;

/*
기본패키지로 설정한 곳에 컨트롤러를 선언하면 요청이 들어왔을때
Auto scan된다. 해당 설정은 servlet-context.xml에서 추가한다.
 */
@Controller
public class BbsController {
	
	/*
	@Autowired
		: 스프링 설정파일에서 이미 생성된 빈을 자동으로 주입받고
		싶을때 사용한다. 타입을 기반으로 자동주입되며 만약 해당 타입의
		빈이 존재하지 않으면 에러가 발생되어 서버를 시작할 수 없다.
		- 생성자, 멤버변수, 메서드(setter)에 적용할 수 있다.
		- 타입을 이용해 자동으로 프로퍼티 값을 설정한다.
		- 해당 어노테이션은 멤버변수에만 적용할 수 있다. 메서드내의
			지역변수에는 사용할 수 없다.
		- 타입을 통해 자동으로 설정되므로 같은 타입이 2개 이상 존재하면
			예외가 발생한다.
	 */
	private JdbcTemplate template;
	
	/*
	servlet-context.xml에서 생성한 빈을 여기서 자동으로 주입받는다.
	 */
	@Autowired
	public void setTemplate(JdbcTemplate template) {
		this.template = template;
		System.out.println("@Autowired -> JDBCTemplate 연결성공");
		
		//JdbcTemplate를 해당 프로그램 전체에서 사용하기 위한 설정(static)
		JdbcTemplateConst.template = this.template;
	}
	
	/*
	멤버변수로 선언하여 클래스에서 전역적으로 사용할 수 있다. 해당
	클래스의 모든 Command(서비스)객체는 해당 인터페이를 구현하여 정의한다.
	 */
	BbsCommandImpl command = null;
	
	//게시판 리스트
	@RequestMapping("board/list.do")
	public String list(Model model, HttpServletRequest req) {
		
		/*
		사용자로부터 받은 모든 요청을 request객체에 저장되고 이를
		ListCommand객체로 전달하기 위해 Model객체에 저장한 후 매개변수로
		전달한다.
		 */
		model.addAttribute("req", req); //request객체 자체를 Model에 저장
		command = new ListCommand(); //service역할의 ListCommand객체 생성
		command.execute(model); //해당 객체로 Model객체 자체를 전달
		
		return "07Board/list";
	}
	
	//글쓰기 페이지로 진입하기 위한 매핑 처리
	@RequestMapping("/board/write.do")
	public String wrtie(Model model) {
		return "07Board/write";
	}
	
	//전송방식이 post이므로 value, method까지 같이 기술해서 매핑
	@RequestMapping(value="/board/writeAction.do", method=RequestMethod.POST)
	public String writeAction(Model model, HttpServletRequest req, SpringBbsDTO springBbsDTO) {
		/*
		글쓰기 페이지에서 전송된 모든 폼값은 SpringBbsDTO 객체를 통해
		한번에 받을 수 있다. Spring에서는 커맨드 객체를 통해 이와같은
		처리를 할 수 있다.
		 */
		
		//request객체와 함께 Model에 저장
		model.addAttribute("req", req);
		model.addAttribute("springBbsDTO", springBbsDTO);
		
		//요청을 전달할 service객체를 생성한 후 execute()메서드 호출
		command = new WriteActionCommand();
		command.execute(model);
		
		//뷰를 반환하지 않고, 지정된 URL(요청명)로 이동한다.
		return "redirect:list.do?nowPage=1";
	}
}

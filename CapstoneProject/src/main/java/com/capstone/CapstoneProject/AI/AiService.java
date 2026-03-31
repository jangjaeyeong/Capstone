package com.capstone.CapstoneProject.AI;

import com.capstone.CapstoneProject.AI.DTO.*;
import com.capstone.CapstoneProject.AI.Entity.InterviewQuestion;
import com.capstone.CapstoneProject.AI.Entity.Roles;
import com.capstone.CapstoneProject.AI.Entity.Tools;
import com.capstone.CapstoneProject.AI.Repository.InterviewQuestionRepository;
import com.capstone.CapstoneProject.AI.Repository.RolesRepository;
import com.capstone.CapstoneProject.AI.Repository.ToolsRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Transactional
@RequiredArgsConstructor
public class AiService {
    private final RestTemplate restTemplate;
    private final ToolsRepository toolsRepository;
    private final RolesRepository rolesRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final HttpHeaders headers = new HttpHeaders();

    //설문으로 결과 보기
    public FrontResponseDTO quizService(QuizRequestDto aiDto) {
        String pythonUrl = "http://localhost:8000/ai/chat";
        Map<String, String> aiResponse = new HashMap<>();
        List<Map<String, String>> messages = new ArrayList<>();
        Map<String, Object> body = new HashMap<>();
        StringBuilder responseBuilder = new StringBuilder();
        List roleList = rolesRepository.findAllRoles();

        responseBuilder.append("조건: ");
        for (QuizAnswerDto answerDto : aiDto.getAnswers()) {
            responseBuilder.append("Q: ").append(answerDto.getQuestion()).append(" ");
            responseBuilder.append("A: ").append(answerDto.getValue()).append("\n");
        }
        responseBuilder.append("\" json 형식의 key값은 role, stacks, roadmap, reasons 로 해줘.  " +
                "이 질문과 답변들을 분석해서 가장 적합한 구체적인 직군은 role, " +
                " stacks에는 해당 직군에 필요한 기술 스택 6개 추천해주는데 언어는 꼭 포함시켜줘." +
                " roadmap에는 상세 학습 로드맵 5단계 추천해주는데 존댓말로 자세하게 알려주고" +
                " reason에는 스택 추천 이유도 알려줘. 추천 이유는 반드시 스택 명 : 이유 형식으로 Map으로 해주고" +
                " 추천 스택 중 언어는 사용해본 언어 말고 배우고싶거나 관심있는 언어를 최우선으로 분석해줘." +
                " 로드맵도 리스트로 줘 결과는 오직 JSON으로만 출력해주는데 설명 덧붙이지 마" +
                " 직군은 ");
        responseBuilder.append(roleList).append("에서 가져와주고 스택은 반드시 ");
        for(String toolName : toolsRepository.findAllTools()) {
            responseBuilder.append(toolName).append(", ");
        }
        responseBuilder.append("에 있는 값으로만 추천해주고, 스택은 서로 연관성 있는거로 추천해줘.")
              .append("스택명끼리 절대 합치지 마. 예를 들어 Java/Spring boot 같이.")
              .append("직군은 배우고 싶은 언어를 가장 우선순위로 두고 흥미로운 프로젝트를 " +
                      "배우고 싶은 언어와 매칭해서 분석해줘.");

        headers.setContentType(MediaType.APPLICATION_JSON);
        aiResponse.put("content", responseBuilder.toString());
        messages.add(aiResponse);

        body.put("messages", messages);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        System.out.println("사용자 답변: " + body);
        ResponseEntity<QuizResponseDTO> res = restTemplate.postForEntity(pythonUrl, entity, QuizResponseDTO.class);
        System.out.println("AI 답변: " + res.getBody());

        res.getBody().getStacks().removeIf(stackName ->
                toolsRepository.findByToolName(stackName).isEmpty());
        FrontResponseDTO frontResponse = FrontResponseQuiz(res.getBody());
        return frontResponse;
    }

    //채팅으로 결과보기
    public Map<String, String > chatService(ChatRequestDTO chatReqDto) {
        String pythonUrl = "http://localhost:8000/rag/chat";
        Map<String, String> aiResponse = new HashMap<>();
        StringBuilder responseBuilder = new StringBuilder();
        HttpEntity<Map<String, String>> entity;

        headers.setContentType(MediaType.APPLICATION_JSON);
        responseBuilder.append("조건: ");
        for(QuizAnswerDto dto : chatReqDto.getQuizAnswers()) {
            responseBuilder.append("Q: ").append(dto.getQuestion()).append(" ");
            responseBuilder.append("A: ").append(dto.getValue()).append("\n");
        }
        responseBuilder.append("이 질문과 답변을 분석해서 추천하는 직군과 스택, 로드맵 알려줘." +
                "스택이랑 로드맵 추천 이유도 같이 알려줘.");
        List<ChatMessageDTO> messagesDto = chatReqDto.getMessages();

        for (int i = 0; i < chatReqDto.getMessages().size(); i ++) {
            ChatMessageDTO dto = messagesDto.get(i);
            if (i != 0) {
                responseBuilder.append(dto.getRole()).append(": ");
                responseBuilder.append(dto.getContent()).append("\n");
            }
        }
            aiResponse.put("question", responseBuilder.toString());
            entity = new HttpEntity<>(aiResponse, headers);
            ResponseEntity<Map> res =
                    restTemplate.postForEntity(pythonUrl, entity, Map.class);
        System.out.println("사용자 조건: "+ aiResponse.values());
        System.out.println("AI Chat 답변: " + res.getBody().toString());
        String value = String.valueOf(res.getBody().values().iterator().next());
        value = value.replace("assistant: ", "");
        Map<String, String> response = new HashMap<>();
        response.put("reply", value);
        return response;
    }

    //AI한테 받은 값 앞단으로 재가공해서 꺼내주기
    public FrontResponseDTO FrontResponseQuiz(QuizResponseDTO quizResDto) {
        List<FrontResponseDTO.StackItem> stack = new ArrayList<>();
        List<FrontResponseDTO.ReasonItem> reason = new ArrayList<>();

        for(String stackName: quizResDto.getStacks()) {
            Tools tools = toolsRepository.findByToolName(stackName).orElse(null);
            String category = tools.getComment();
            String icon = null;
            FrontResponseDTO.StackItem stackItem = new FrontResponseDTO.StackItem
                    (stackName, category, icon);
            FrontResponseDTO.ReasonItem reasonItem = new FrontResponseDTO.ReasonItem(
                    stackName, quizResDto.getReasons().get(stackName));
            stack.add(stackItem);
            reason.add(reasonItem);
        }
        Roles roleName = rolesRepository.findByRoleName(quizResDto.getRole().toString()).orElse(null);
        FrontResponseDTO response = FrontResponseDTO.builder()
                .role(quizResDto.getRole())
                .summary(roleName.getComment())
                .roleIcon(null)
                .roadmap(quizResDto.getRoadmap())
                .reasons(reason)
                .stacks(stack)
                .build();
        return response;
    }

    public List<InterviewQuestionResDTO> interviewQuestionList (){
        List<InterviewQuestion> questions = interviewQuestionRepository.findAll();

        return questions.stream().map(question -> new InterviewQuestionResDTO(
                question.getId(),
                question.getCategory(),
                question.getQuestion()
        )).collect(Collectors.toList());

    }
}
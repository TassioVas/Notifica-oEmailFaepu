package br.org.faepu.notificacaoAuditoriaDevolver;

import java.math.BigDecimal;
import java.sql.Timestamp;

import br.com.sankhya.jape.EntityFacade;
import br.com.sankhya.jape.bmp.PersistentLocalEntity;
import br.com.sankhya.jape.core.JapeSession;
import br.com.sankhya.jape.dao.JdbcWrapper;
import br.com.sankhya.jape.sql.NativeSql;
import br.com.sankhya.jape.vo.DynamicVO;
import br.com.sankhya.jape.vo.EntityVO;
import br.com.sankhya.jape.wrapper.JapeFactory;
import br.com.sankhya.modelcore.util.EntityFacadeFactory;

public class EnviaEmail {
	
	String msg;
	String corpoEmail;
	
	BigDecimal codFila = null;
	
	Timestamp agora = new Timestamp(System.currentTimeMillis());
	
	public void EnviarEmail(BigDecimal usuarioLogado, BigDecimal nuFin, String emailNota, String nomeUsuNota, BigDecimal nuNota, String obs) {
		
		JdbcWrapper jdbc = JapeFactory.getEntityFacade().getJdbcWrapper();
		NativeSql nativeSql = new NativeSql(jdbc);
		
		EntityFacade dwfEntityFacade = EntityFacadeFactory.getDWFFacade();
		jdbc = dwfEntityFacade.getJdbcWrapper();
		JapeSession.SessionHandle hnd = JapeSession.open();
		
		System.out.println("Entrou no metodo enviar email.");
		
		System.out.println("OBS :" + obs);
		System.out.println("email :" + emailNota);
		
		corpoEmail = " <h2>AUDITORIA</h2>\r\n"
				+ "			<p><span style=\"font-size:14px\"><b>A NOTA DO NÚMERO ÚNICO DE ORIGEM :</b>\r\n"
				+ "			 <span style=\"font-size:16px\"><span style=\"color:#FF0000\"><b>"+nuNota+"</span></span></span></b><span style=\"font-size:14px\">\r\n"
				+ "		      <b>Após Correção pela Rotina de Notificação, esta sendo devolvido pelo "+nomeUsuNota+". </b></span><span style=\"font-size:16px\"> ";
		
		
		char[] corpoEmailchar = corpoEmail.toCharArray();
		
		try {
			DynamicVO filaMensagemVO = (DynamicVO) dwfEntityFacade
					.getDefaultValueObjectInstance("MSDFilaMensagem");
			filaMensagemVO.setProperty("ASSUNTO", "Notificação devolução Auditoria");
			filaMensagemVO.setProperty("CODMSG", null);
			filaMensagemVO.setProperty("DTENTRADA", agora);
			filaMensagemVO.setProperty("STATUS", "Pendente");
			filaMensagemVO.setProperty("CODCON", new BigDecimal(0));
			filaMensagemVO.setProperty("TENTENVIO", new BigDecimal(0));
			filaMensagemVO.setProperty("MENSAGEM", corpoEmailchar);
			filaMensagemVO.setProperty("TIPOENVIO", "E");
			filaMensagemVO.setProperty("MAXTENTENVIO", new BigDecimal(3));
			//filaMensagemVO.setProperty("EMAIL", "t.santos.vasconcelos@gmail.com");
			filaMensagemVO.setProperty("EMAIL", "auditoria@gmail.com");
		    //filaMensagemVO.setProperty("EMAIL", "tassio@faepu.org.br");
		    //filaMensagemVO.setProperty("EMAIL", emailNota); // aqui 
			//filaMensagemVO.setProperty("CODSMTP", new BigDecimal(2));
			filaMensagemVO.setProperty("CODUSUREMET", usuarioLogado);

			PersistentLocalEntity createFilaMensagem = dwfEntityFacade.createEntity("MSDFilaMensagem",
					(EntityVO) filaMensagemVO);
			filaMensagemVO = (DynamicVO) createFilaMensagem.getValueObject();
			codFila = filaMensagemVO.asBigDecimal("CODFILA");
			
		}  catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}
		
		try {

			System.out.println("CODFILA na inserção do item" + codFila);
			System.out.println("alteração do cod");

			EntityFacade dwfFacade = EntityFacadeFactory.getDWFFacade();
			DynamicVO dynamicVO1 = (DynamicVO) dwfFacade.getDefaultValueObjectInstance("MSDDestFilaMensagem");

			dynamicVO1.setProperty("CODFILA", codFila);
			dynamicVO1.setProperty("EMAIL", "wanderson@faepu.org.br");
			//dynamicVO1.setProperty("EMAIL", "t.santos.vasconcelos@gmail.com");
			//dynamicVO1.setProperty("SEQUENCIA", seq);
			PersistentLocalEntity createEntity = dwfFacade.createEntity("MSDDestFilaMensagem",
					(EntityVO) dynamicVO1);
			DynamicVO save = (DynamicVO) createEntity.getValueObject();

			System.out.println("CODFILA " + codFila);

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Erro na inclusao do item " + e.getMessage();
			System.out.println(msg);
		}
		
		JapeSession.close(hnd);
	}
}

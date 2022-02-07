package com.ssafy.DTO;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookDTO {
	private int id;
	private String name;
	private String author;
	private String contents;
	private String publisher;
	private String isbn;
	private int pubYear;
	private int loanCount;
	private int genreId;
	private String thumbnailUrl;
}

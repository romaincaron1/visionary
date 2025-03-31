package com.romaincaron.data_collection.util;

public class AniListQueries {

    public static final String GET_MEDIA_BY_ID = """
        query ($id: Int, $type: MediaType) {
          Media(id: $id, type: $type) {
            id
            title {
              romaji
              english
              native
            }
            description
            startDate {
              year
              month
              day
            }
            endDate {
              year
              month
              day
            }
            format
            status
            chapters
            volumes
            coverImage {
              large
              medium
            }
            staff {
              nodes {
                name {
                  full
                }
              }
            }
            tags {
              id
              name
              rank
            }
            genres
            meanScore
            popularity
            countryOfOrigin
            isAdult
          }
        }
    """;

    public static final String GET_RECENT_MEDIA = """
        query ($page: Int, $perPage: Int, $type: MediaType, $sort: [MediaSort]) {
          Page(page: $page, perPage: $perPage) {
            pageInfo {
              total
              currentPage
              lastPage
              hasNextPage
            }
            media(type: $type, sort: $sort) {
              id
              title {
                romaji
                english
                native
              }
              description
              startDate {
                year
                month
                day
              }
              endDate {
                year
                month
                day
              }
              format
              status
              chapters
              volumes
              coverImage {
                large
                medium
              }
              tags {
                id
                name
                rank
              }
              genres
              meanScore
              popularity
              countryOfOrigin
              isAdult
            }
          }
        }
    """;

    public static final String GET_MEDIA_PAGE = """
    query ($page: Int, $perPage: Int, $type: MediaType, $sort: [MediaSort]) {
      Page(page: $page, perPage: $perPage) {
        pageInfo {
          total
          currentPage
          lastPage
          hasNextPage
        }
        media(type: $type, sort: $sort) {
          id
          title {
            romaji
            english
            native
          }
          description
          startDate {
            year
            month
            day
          }
          endDate {
            year
            month
            day
          }
          format
          status
          chapters
          volumes
          coverImage {
            large
            medium
          }
          tags {
            id
            name
            rank
          }
          genres
          meanScore
          popularity
          countryOfOrigin
          isAdult
        }
      }
    }
    """;
}

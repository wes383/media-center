# IMDB Clone API 文档

本文档为 IMDB Clone 项目后端 API 提供了详细的说明，旨在帮助前端开发人员理解和使用这些接口。

## 统一响应结构

所有 API 的成功响应都遵循统一的 JSON 结构：

```json
{
  "data": {
    // 具体的业务数据
  }
}
```

---

## 1. 获取影片列表 (高级搜索)

此端点用于根据多个筛选条件获取影片列表，支持分页和排序。

- **Endpoint**: `GET /api/v1/titles`
- **功能**: 获取一个经过筛选和排序的影片列表。

### 查询参数 (Query Parameters)

| 参数 | 类型 | 描述 | 示例 | 默认值 |
| :--- | :--- | :--- | :--- | :--- |
| `titleType` | string | 筛选影片类型。 | `movie` | |
| `genre` | string | 按类型筛选，多个类型用逗号分隔。 | `Action,Drama` | |
| `startYear` | integer | 筛选此年份之后（包含此年）上映的影片。 | `2020` | |
| `minRating` | float | 筛选平均分不低于此值的影片。 | `8.0` | |
| `minVotes` | integer | 筛选投票数不低于此值的影片。 | `100000` | |
| `sortBy` | string | 排序依据。可选值：`popularity` (按热度/投票数降序), `rating` (按评分降序), `releaseDate` (按上映年份降序)。 | `popularity` | |
| `search` | string | 按影片主标题 (`primaryTitle`) 进行模糊搜索。 | `Inception` | |
| `limit` | integer | 每页返回的数量。 | `20` | `20` |
| `offset` | integer | 分页偏移量，用于获取后续页面的数据。 | `0` | `0` |

### 示例请求

- 获取最受欢迎的20部动作片:
  `/api/v1/titles?titleType=movie&genre=Action&sortBy=popularity&limit=20`
- 搜索2020年后评分高于8.0的剧集:
  `/api/v1/titles?titleType=tvSeries&startYear=2020&minRating=8.0`

### 响应 JSON 结构 (示例)

```json
{
  "data": [
    {
      "tconst": "tt1375666",
      "titleType": "movie",
      "primaryTitle": "Inception",
      "originalTitle": "Inception",
      "isAdult": false,
      "startYear": 2010,
      "endYear": null,
      "runtimeMinutes": 148,
      "averageRating": 8.8,
      "numVotes": 2400000
    }
  ]
}
```

---

## 2. 获取影片详情

根据影片的唯一 ID (`tconst`) 获取其所有详细信息，包括演职员列表。

- **Endpoint**: `GET /api/v1/titles/{tconst}`
- **功能**: 获取指定影片的详细信息。

### 路径参数 (Path Variables)

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| `tconst` | string | 影片的唯一标识符。 |

### 示例请求

`/api/v1/titles/tt0111161`

### 响应 JSON 结构 (示例)

```json
{
  "data": {
    "tconst": "tt0111161",
    "titleType": "movie",
    "primaryTitle": "The Shawshank Redemption",
    "startYear": 1994,
    "runtimeMinutes": 142,
    "averageRating": 9.3,
    "numVotes": 2700000,
    "genres": ["Drama"],
    "principals": [
      {
        "nconst": "nm0000209",
        "primaryName": "Tim Robbins",
        "category": "actor",
        "job": null,
        "characters": "[\"Andy Dufresne\"]"
      },
      {
        "nconst": "nm0000151",
        "primaryName": "Morgan Freeman",
        "category": "actor",
        "job": null,
        "characters": "[\"Ellis Boyd 'Red' Redding\"]"
      },
      {
        "nconst": "nm0001104",
        "primaryName": "Frank Darabont",
        "category": "director",
        "job": null,
        "characters": null
      }
    ]
  }
}
```

```JSON
{
    "tconst": "tt0903747",
    "titleType": "tvSeries",
    "primaryTitle": "Breaking Bad",
    "startYear": 2008,
    "runtimeMinutes": 45,
    "averageRating": 9.5,
    "numVotes": 2398648,
    "genres": [
        "Drama",
        "Thriller",
        "Crime"
    ],
    "principals": [
        {
            "nconst": "nm0186505",
            "primaryName": "Bryan Cranston",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Walter White\"]"
        },
        {
            "nconst": "nm0666739",
            "primaryName": "Aaron Paul",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Jesse Pinkman\"]"
        },
        {
            "nconst": "nm0348152",
            "primaryName": "Anna Gunn",
            "category": "actress",
            "job": "\\N",
            "characters": "[\"Skyler White\"]"
        },
        {
            "nconst": "nm1336827",
            "primaryName": "Betsy Brandt",
            "category": "actress",
            "job": "\\N",
            "characters": "[\"Marie Schrader\"]"
        },
        {
            "nconst": "nm2666409",
            "primaryName": "RJ Mitte",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Walter White, Jr.\"]"
        },
        {
            "nconst": "nm0606487",
            "primaryName": "Dean Norris",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Hank Schrader\"]"
        },
        {
            "nconst": "nm0644022",
            "primaryName": "Bob Odenkirk",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Saul Goodman\"]"
        },
        {
            "nconst": "nm2366374",
            "primaryName": "Steven Michael Quezada",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Steven Gomez\"]"
        },
        {
            "nconst": "nm0052186",
            "primaryName": "Jonathan Banks",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Mike Ehrmantraut\"]"
        },
        {
            "nconst": "nm0002064",
            "primaryName": "Giancarlo Esposito",
            "category": "actor",
            "job": "\\N",
            "characters": "[\"Gus Fring\"]"
        },
        {
            "nconst": "nm0319213",
            "primaryName": "Vince Gilligan",
            "category": "writer",
            "job": "created by",
            "characters": "\\N"
        }
    ],
    "seasons": [
        {
            "seasonNumber": 1,
            "episodes": [
                {
                    "tconst": "tt0959621",
                    "episodeNumber": 1,
                    "primaryTitle": "Pilot",
                    "averageRating": 9.0
                },
                {
                    "tconst": "tt1054724",
                    "episodeNumber": 2,
                    "primaryTitle": "Cat's in the Bag...",
                    "averageRating": 8.6
                },
                {
                    "tconst": "tt1054725",
                    "episodeNumber": 3,
                    "primaryTitle": "...And the Bag's in the River",
                    "averageRating": 8.7
                },
                {
                    "tconst": "tt1054726",
                    "episodeNumber": 4,
                    "primaryTitle": "Cancer Man",
                    "averageRating": 8.2
                },
                {
                    "tconst": "tt1054727",
                    "episodeNumber": 5,
                    "primaryTitle": "Gray Matter",
                    "averageRating": 8.3
                },
                {
                    "tconst": "tt1054728",
                    "episodeNumber": 6,
                    "primaryTitle": "Crazy Handful of Nothin'",
                    "averageRating": 9.3
                },
                {
                    "tconst": "tt1054729",
                    "episodeNumber": 7,
                    "primaryTitle": "A No-Rough-Stuff-Type Deal",
                    "averageRating": 8.8
                }
            ]
        },
        {
            "seasonNumber": 2,
            "episodes": [
                {
                    "tconst": "tt1232244",
                    "episodeNumber": 1,
                    "primaryTitle": "Seven Thirty-Seven",
                    "averageRating": 8.6
                },
                {
                    "tconst": "tt1232249",
                    "episodeNumber": 2,
                    "primaryTitle": "Grilled",
                    "averageRating": 9.3
                },
                {
                    "tconst": "tt1232250",
                    "episodeNumber": 3,
                    "primaryTitle": "Bit by a Dead Bee",
                    "averageRating": 8.3
                },
                {
                    "tconst": "tt1232251",
                    "episodeNumber": 4,
                    "primaryTitle": "Down",
                    "averageRating": 8.2
                },
                {
                    "tconst": "tt1232252",
                    "episodeNumber": 5,
                    "primaryTitle": "Breakage",
                    "averageRating": 8.2
                },
                {
                    "tconst": "tt1232253",
                    "episodeNumber": 6,
                    "primaryTitle": "Peekaboo",
                    "averageRating": 8.8
                },
                {
                    "tconst": "tt1232254",
                    "episodeNumber": 7,
                    "primaryTitle": "Negro y Azul",
                    "averageRating": 8.6
                },
                {
                    "tconst": "tt1232255",
                    "episodeNumber": 8,
                    "primaryTitle": "Better Call Saul",
                    "averageRating": 9.2
                },
                {
                    "tconst": "tt1232256",
                    "episodeNumber": 9,
                    "primaryTitle": "4 Days Out",
                    "averageRating": 9.2
                },
                {
                    "tconst": "tt1232245",
                    "episodeNumber": 10,
                    "primaryTitle": "Over",
                    "averageRating": 8.4
                },
                {
                    "tconst": "tt1232246",
                    "episodeNumber": 11,
                    "primaryTitle": "Mandala",
                    "averageRating": 8.9
                },
                {
                    "tconst": "tt1232247",
                    "episodeNumber": 12,
                    "primaryTitle": "Phoenix",
                    "averageRating": 9.3
                },
                {
                    "tconst": "tt1232248",
                    "episodeNumber": 13,
                    "primaryTitle": "ABQ",
                    "averageRating": 9.2
                }
            ]
        },
        {
            "seasonNumber": 3,
            "episodes": [
                {
                    "tconst": "tt1528116",
                    "episodeNumber": 1,
                    "primaryTitle": "No Más",
                    "averageRating": 8.5
                },
                {
                    "tconst": "tt1615186",
                    "episodeNumber": 2,
                    "primaryTitle": "Caballo sin Nombre",
                    "averageRating": 8.6
                },
                {
                    "tconst": "tt1615187",
                    "episodeNumber": 3,
                    "primaryTitle": "I.F.T.",
                    "averageRating": 8.4
                },
                {
                    "tconst": "tt1615554",
                    "episodeNumber": 4,
                    "primaryTitle": "Green Light",
                    "averageRating": 8.1
                },
                {
                    "tconst": "tt1615555",
                    "episodeNumber": 5,
                    "primaryTitle": "Más",
                    "averageRating": 8.5
                },
                {
                    "tconst": "tt1615556",
                    "episodeNumber": 6,
                    "primaryTitle": "Sunset",
                    "averageRating": 9.3
                },
                {
                    "tconst": "tt1615944",
                    "episodeNumber": 7,
                    "primaryTitle": "One Minute",
                    "averageRating": 9.6
                },
                {
                    "tconst": "tt1615557",
                    "episodeNumber": 8,
                    "primaryTitle": "I See You",
                    "averageRating": 8.7
                },
                {
                    "tconst": "tt1615558",
                    "episodeNumber": 9,
                    "primaryTitle": "Kafkaesque",
                    "averageRating": 8.4
                },
                {
                    "tconst": "tt1615550",
                    "episodeNumber": 10,
                    "primaryTitle": "Fly",
                    "averageRating": 7.9
                },
                {
                    "tconst": "tt1615551",
                    "episodeNumber": 11,
                    "primaryTitle": "Abiquiu",
                    "averageRating": 8.4
                },
                {
                    "tconst": "tt1615552",
                    "episodeNumber": 12,
                    "primaryTitle": "Half Measures",
                    "averageRating": 9.5
                },
                {
                    "tconst": "tt1615553",
                    "episodeNumber": 13,
                    "primaryTitle": "Full Measure",
                    "averageRating": 9.7
                }
            ]
        },
        {
            "seasonNumber": 4,
            "episodes": [
                {
                    "tconst": "tt1683084",
                    "episodeNumber": 1,
                    "primaryTitle": "Box Cutter",
                    "averageRating": 9.2
                },
                {
                    "tconst": "tt1683089",
                    "episodeNumber": 2,
                    "primaryTitle": "Thirty-Eight Snub",
                    "averageRating": 8.2
                },
                {
                    "tconst": "tt1683090",
                    "episodeNumber": 3,
                    "primaryTitle": "Open House",
                    "averageRating": 8.0
                },
                {
                    "tconst": "tt1683091",
                    "episodeNumber": 4,
                    "primaryTitle": "Bullet Points",
                    "averageRating": 8.5
                },
                {
                    "tconst": "tt1683092",
                    "episodeNumber": 5,
                    "primaryTitle": "Shotgun",
                    "averageRating": 8.6
                },
                {
                    "tconst": "tt1683093",
                    "episodeNumber": 6,
                    "primaryTitle": "Cornered",
                    "averageRating": 8.4
                },
                {
                    "tconst": "tt1683094",
                    "episodeNumber": 7,
                    "primaryTitle": "Problem Dog",
                    "averageRating": 8.8
                },
                {
                    "tconst": "tt1683095",
                    "episodeNumber": 8,
                    "primaryTitle": "Hermanos",
                    "averageRating": 9.3
                },
                {
                    "tconst": "tt1683096",
                    "episodeNumber": 9,
                    "primaryTitle": "Bug",
                    "averageRating": 8.9
                },
                {
                    "tconst": "tt1683085",
                    "episodeNumber": 10,
                    "primaryTitle": "Salud",
                    "averageRating": 9.6
                },
                {
                    "tconst": "tt1683086",
                    "episodeNumber": 11,
                    "primaryTitle": "Crawl Space",
                    "averageRating": 9.7
                },
                {
                    "tconst": "tt1683087",
                    "episodeNumber": 12,
                    "primaryTitle": "End Times",
                    "averageRating": 9.5
                },
                {
                    "tconst": "tt1683088",
                    "episodeNumber": 13,
                    "primaryTitle": "Face Off",
                    "averageRating": 9.9
                }
            ]
        },
        {
            "seasonNumber": 5,
            "episodes": [
                {
                    "tconst": "tt2081647",
                    "episodeNumber": 1,
                    "primaryTitle": "Live Free or Die",
                    "averageRating": 9.2
                },
                {
                    "tconst": "tt2301457",
                    "episodeNumber": 2,
                    "primaryTitle": "Madrigal",
                    "averageRating": 8.8
                },
                {
                    "tconst": "tt2301459",
                    "episodeNumber": 3,
                    "primaryTitle": "Hazard Pay",
                    "averageRating": 8.8
                },
                {
                    "tconst": "tt2301461",
                    "episodeNumber": 4,
                    "primaryTitle": "Fifty-One",
                    "averageRating": 8.8
                },
                {
                    "tconst": "tt2301463",
                    "episodeNumber": 5,
                    "primaryTitle": "Dead Freight",
                    "averageRating": 9.7
                },
                {
                    "tconst": "tt2301465",
                    "episodeNumber": 6,
                    "primaryTitle": "Buyout",
                    "averageRating": 9.0
                },
                {
                    "tconst": "tt2301467",
                    "episodeNumber": 7,
                    "primaryTitle": "Say My Name",
                    "averageRating": 9.6
                },
                {
                    "tconst": "tt2301469",
                    "episodeNumber": 8,
                    "primaryTitle": "Gliding Over All",
                    "averageRating": 9.6
                },
                {
                    "tconst": "tt2301471",
                    "episodeNumber": 9,
                    "primaryTitle": "Blood Money",
                    "averageRating": 9.4
                },
                {
                    "tconst": "tt2301443",
                    "episodeNumber": 10,
                    "primaryTitle": "Buried",
                    "averageRating": 9.2
                },
                {
                    "tconst": "tt2301445",
                    "episodeNumber": 11,
                    "primaryTitle": "Confessions",
                    "averageRating": 9.6
                },
                {
                    "tconst": "tt2301447",
                    "episodeNumber": 12,
                    "primaryTitle": "Rabid Dog",
                    "averageRating": 9.2
                },
                {
                    "tconst": "tt2301449",
                    "episodeNumber": 13,
                    "primaryTitle": "To'hajiilee",
                    "averageRating": 9.8
                },
                {
                    "tconst": "tt2301451",
                    "episodeNumber": 14,
                    "primaryTitle": "Ozymandias",
                    "averageRating": 10.0
                },
                {
                    "tconst": "tt2301453",
                    "episodeNumber": 15,
                    "primaryTitle": "Granite State",
                    "averageRating": 9.7
                },
                {
                    "tconst": "tt2301455",
                    "episodeNumber": 16,
                    "primaryTitle": "Felina",
                    "averageRating": 9.9
                }
            ]
        }
    ]
}
```

---

## 3. 获取影人详情及其作品

根据影人的唯一 ID (`nconst`) 获取其生平信息和参与的影片列表。

- **Endpoint**: `GET /api/v1/names/{nconst}`
- **功能**: 获取指定影人的详细信息和作品履历。

### 路径参数 (Path Variables)

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| `nconst` | string | 影人的唯一标识符。 |

### 示例请求

`/api/v1/names/nm0000138`

### 响应 JSON 结构 (示例)

```json
{
  "data": {
    "nconst": "nm0000138",
    "primaryName": "Leonardo DiCaprio",
    "birthYear": 1974,
    "deathYear": null,
    "primaryProfessions": ["actor", "producer", "writer"],
    "filmography": [
      {
        "tconst": "tt1375666",
        "primaryTitle": "Inception",
        "startYear": 2010,
        "category": "actor",
        "characters": "[\"Cobb\"]"
      },
      {
        "tconst": "tt0120338",
        "primaryTitle": "Titanic",
        "startYear": 1997,
        "category": "actor",
        "characters": "[\"Jack Dawson\"]"
      }
    ]
  }
}
```

---

## 4. 全局搜索

一个统一的搜索入口，可以同时模糊搜索影片和影人。

- **Endpoint**: `GET /api/v1/search`
- **功能**: 根据关键词搜索影片和影人。

### 查询参数 (Query Parameters)

| 参数 | 类型 | 描述 | 示例 |
| :--- | :--- | :--- | :--- |
| `query` | string | 搜索关键词 (必需)。 | `dune` |

### 示例请求

`/api/v1/search?query=dune`

### 响应 JSON 结构 (示例)

```json
{
  "data": {
    "titles": [
      { "tconst": "tt1160419", "primaryTitle": "Dune", "startYear": 2021 },
      { "tconst": "tt0087182", "primaryTitle": "Dune", "startYear": 1984 }
    ],
    "names": [
      { "nconst": "nm0222090", "primaryName": "Colleen Dune" }
    ]
  }
}
```

---

## 5. 获取所有影片类型

获取数据库中所有可用的影片类型，用于在前端 UI 中生成筛选器。

- **Endpoint**: `GET /api/v1/genres`
- **功能**: 获取所有影片类型列表。

### 示例请求

`/api/v1/genres`

### 响应 JSON 结构 (示例)

```json
{
  "data": ["Action", "Adventure", "Animation", "Biography", "Comedy", "Crime", "Drama", "Family", "Fantasy", "History", "Horror", "Music", "Mystery", "Romance", "Sci-Fi", "Sport", "Thriller", "War", "Western"]
}
```

---

## 6. 个人观看历史 API

以下端点用于管理和查询用户的个人观看历史。

### 6.1 添加或更新观看记录

将一部影片标记为“已看”并选择性地给出评分。如果记录已存在，则更新评分。

- **Endpoint**: `POST /api/v1/me/history`
- **功能**: 添加一部新影片到观看历史，或更新一部已存在影片的评分。

#### 请求 Body (JSON)

```json
{
  "tconst": "tt0111161",
  "rating": 9
}
```

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| `tconst` | string | **必需。** 影片的唯一 ID。 |
| `rating` | integer | *可选。* 您的评分 (1-10)。 |

#### 成功响应 (200 OK)

返回被操作记录的最新状态。

```json
{
  "data": {
    "tconst": "tt0111161",
    "rating": 9,
    "addedAt": "2025-09-21T10:30:00Z"
  }
}
```

### 6.2 获取观看历史列表

获取“我”看过的影片列表，支持丰富的筛选、排序和分页。

- **Endpoint**: `GET /api/v1/me/history`
- **功能**: 获取经过筛选和排序的个人观看历史列表。

#### 查询参数 (Query Parameters)

| 参数 | 类型 | 描述 | 示例 | 默认值 |
| :--- | :--- | :--- | :--- | :--- |
| `titleType` | string | 筛选影片类型。 | `movie` | |
| `genre` | string | 按类型筛选，多个用逗号分隔。 | `Sci-Fi,Action` | |
| `startYear` | integer | 筛选此年份之后上映的影片。 | `2020` | |
| `minRating` | float | 筛选 IMDb 平均分不低于此值的影片。 | `8.0` | |
| `sortBy` | string | 排序依据。可选值：`popularity`, `rating`, `releaseDate`, `my_rating`, `added_at`。 | `my_rating` | `added_at` |
| `limit` | integer | 每页返回的数量。 | `20` | `20` |
| `offset` | integer | 分页偏移量。 | `0` | `0` |

#### 示例请求

- 查询我看过的、2020年后上映的、IMDb评分高于8分的科幻电影，按热度排序:
  `/api/v1/me/history?startYear=2020&minRating=8.0&genre=Sci-Fi&sortBy=popularity`
- 查询我看过的所有剧集中，我自己评分最高的:
  `/api/v1/me/history?titleType=tvSeries&sortBy=my_rating`

#### 成功响应 (200 OK)

```json
{
  "data": [
    {
      "tconst": "tt0903747",
      "primaryTitle": "Breaking Bad",
      "startYear": 2008,
      "averageRating": 9.5,
      "numVotes": 1800000,
      "myRating": 10,
      "addedAt": "2025-09-20T12:00:00Z"
    }
  ],
  "metadata": {
    "total": 1,
    "limit": 20,
    "offset": 0
  }
}
```

### 6.3 查询单部影片的观看状态

检查某一部特定影片是否在您的观看历史中，并获取其记录。

- **Endpoint**: `GET /api/v1/me/history/{tconst}`
- **功能**: 获取单部影片的观看状态和评分。

#### 路径参数 (Path Variables)

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| `tconst` | string | 影片的唯一标识符。 |

#### 成功响应 (200 OK)

如果影片在观看历史中，返回记录详情。

```json
{
  "tconst": "tt0111161",
  "rating": 9,
  "addedAt": "2025-09-21T10:30:00Z"
}
```

#### 失败响应 (404 Not Found)

如果影片不在观看历史中，返回 `404 Not Found` 状态码和空响应体。

### 6.4 移除观看记录

从您的观看历史中彻底删除一部影片。

- **Endpoint**: `DELETE /api/v1/me/history/{tconst}`
- **功能**: 从观看历史中删除一部影片。

#### 路径参数 (Path Variables)

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| `tconst` | string | 影片的唯一标识符。 |

#### 成功响应 (204 No Content)

操作成功，返回 `204 No Content` 状态码和空响应体。

---

## 7. 获取剧集所属的电视剧信息

根据剧集的 `tconst` ID，获取其所属的电视剧（剧集）的概要信息，以及它在该剧中的季号和集号。

- **Endpoint**: `GET /api/v1/episodes/{tconst}/source`
- **功能**: 获取单集剧集所属的父剧集信息。

### 路径参数 (Path Variables)

| 参数 | 类型 | 描述 |
| :--- | :--- | :--- |
| `tconst` | string | 剧集分集的唯一标识符。 |

### 示例请求

`/api/v1/episodes/tt0959621/source`

### 响应 JSON 结构 (示例)

```json
{
  "data": {
    "seriesTconst": "tt0903747",
    "seriesPrimaryTitle": "Breaking Bad",
    "seriesStartYear": 2008,
    "seasonNumber": 2,
    "episodeNumber": 6
  }
}
```

### 失败响应 (404 Not Found)

如果提供的 `tconst` 不存在，或者它不是一个剧集分集，将返回 `404 Not Found`。